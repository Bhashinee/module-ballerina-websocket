// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/runtime;
import ballerina/test;
import ballerina/io;
import ballerina/websocket;

string expectedString = "";
byte[] expectedBinaryData = [];
service sslEcho on new websocket:Listener(21029, {
        secureSocket: {
            keyStore: {
                path: "tests/certsAndKeys/ballerinaKeystore.p12",
                password: "ballerina"
            }
        }
    }) {

    resource function onText(websocket:WebSocketCaller caller, string data, boolean finalFrame) {
        var returnVal = caller->pushText(data, finalFrame);
        if (returnVal is websocket:WebSocketError) {
            panic <error>returnVal;
        }
    }

    resource function onBinary(websocket:WebSocketCaller caller, byte[] data, boolean finalFrame) {
        var returnVal = caller->pushBinary(data, finalFrame);
        if (returnVal is websocket:WebSocketError) {
            panic <error>returnVal;
        }
    }
}

service sslEchoCallbackService = @websocket:WebSocketServiceConfig {} service {
    resource function onText(websocket:WebSocketClient wsEp, string text) {
        expectedString = <@untainted>text;
    }

    resource function onBinary(websocket:WebSocketClient wsEp, byte[] data) {
        expectedBinaryData = <@untainted>data;
    }

    resource function onClose(websocket:WebSocketClient wsEp, int statusCode, string reason) {
        var returnVal = wsEp->close(statusCode = statusCode, reason = reason);
        if (returnVal is websocket:WebSocketError) {
            panic <error>returnVal;
        }
    }
};

// Tests sending and receiving of binary frames in WebSocket.
@test:Config {}
public function sslBinaryEcho() {
    websocket:WebSocketClient wsClient = new ("wss://localhost:21029/sslEcho", {
            callbackService: sslEchoCallbackService,
            secureSocket: {
                trustStore: {
                    path: "tests/certsAndKeys/ballerinaTruststore.p12",
                    password: "ballerina"
                }
            }
        });
    byte[] binaryData = [5, 24, 56];
    checkpanic wsClient->pushBinary(binaryData);
    runtime:sleep(500);
    test:assertEquals(expectedBinaryData, binaryData, msg = "Data mismatched");
    error? result = wsClient->close(statusCode = 1000, reason = "Close the connection");
    if (result is websocket:WebSocketError) {
       io:println("Error occurred when closing connection", result);
    }
}

// Tests sending and receiving of text frames in WebSockets.
@test:Config {}
public function sslTextEcho() {
    websocket:WebSocketClient wsClient = new ("wss://localhost:21029/sslEcho", {
            callbackService: sslEchoCallbackService,
            secureSocket: {
                trustStore: {
                    path: "tests/certsAndKeys/ballerinaTruststore.p12",
                    password: "ballerina"
                }
            }
        });
    checkpanic wsClient->pushText("Hi madam");
    runtime:sleep(500);
    test:assertEquals(expectedString, "Hi madam", msg = "Data mismatched");
    error? result = wsClient->close(statusCode = 1000, reason = "Close the connection");
    if (result is websocket:WebSocketError) {
       io:println("Error occurred when closing connection", result);
    }
}
