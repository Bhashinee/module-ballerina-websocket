//// Copyright (c) 2020 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
////
//// WSO2 Inc. licenses this file to you under the Apache License,
//// Version 2.0 (the "License"); you may not use this file except
//// in compliance with the License.
//// You may obtain a copy of the License at
////
//// http://www.apache.org/licenses/LICENSE-2.0
////
//// Unless required by applicable law or agreed to in writing,
//// software distributed under the License is distributed on an
//// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
//// KIND, either express or implied.  See the License for the
//// specific language governing permissions and limitations
//// under the License.
//
//import ballerina/auth;
//import ballerina/runtime;
//import ballerina/test;
//import ballerina/io;
//import ballerina/websocket;
//
//string expectedOutput39 = "";
//string expectedError39 = "";
//
//@websocket:WebSocketServiceConfig {
//}
//service on new websocket:Listener(21039) {
//
//    resource function onOpen(websocket:WebSocketCaller wsEp) {
//        auth:OutboundBasicAuthProvider outboundBasicAuthProvider = new ({
//            username: "isuru",
//            password: "1234"
//        });
//
//        string token = checkpanic outboundBasicAuthProvider.generateToken();
//
//        websocket:WebSocketClient wsClient = new ("ws://localhost:21040/auth/ws",
//                config = {
//                callbackService: authenticationService,
//                customHeaders: {
//                    [websocket:AUTH_HEADER]: string `Basic ${token}`
//                }
//            }
//            );
//        checkpanic wsClient->pushText("Hello World!");
//    }
//}
//
//service authenticationService = @websocket:WebSocketServiceConfig {} service {
//
//    resource function onText(websocket:WebSocketClient conn, string text, boolean finalFrame) {
//        expectedOutput39 = <@untainted>text;
//    }
//
//    resource function onError(websocket:WebSocketClient conn, error err) {
//        expectedError39 = <@untainted>err.message();
//    }
//};
//
//// Tests with correct credential
//// https://github.com/ballerina-platform/module-ballerina-http/issues/71
//@test:Config {enable : false}
//public function testBasicAuthenticationSuccess() {
//    websocket:WebSocketClient wsClientEp = new ("ws://localhost:21039");
//    runtime:sleep(500);
//    test:assertEquals(expectedOutput39, "Hello World!");
//    error? result = wsClientEp->close(statusCode = 1000, reason = "Close the connection");
//    if (result is websocket:WebSocketError) {
//       io:println("Error occurred when closing connection", result);
//    }
//}
