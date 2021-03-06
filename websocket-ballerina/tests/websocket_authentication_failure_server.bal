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
//import ballerina/io;
//import ballerina/websocket;
//
//websocket:BasicAuthHandler basicAuthHandler = new (new auth:InboundBasicAuthProvider());
//
//listener websocket:Listener httpServ = new (21042, config = {
//    auth: {
//        authHandlers: [inboundBasicAuthHandler],
//        mandateSecureSocket: false
//    }
//});
//
//@websocket:ServiceConfig {
//    basePath: "/auth"
//}
//service upgradeServ on httpServ {
//
//    @websocket:ResourceConfig {
//        webSocketUpgrade: {
//            upgradePath: "/ws",
//            upgradeService: upgradedServ
//        }
//    }
//    resource function upgrader(websocket:Caller caller, websocket:Request req) {
//    }
//}
//service upgradedServ = @websocket:ServiceConfig {} service {
//
//    resource function onOpen(websocket:WebSocketCaller caller) {
//        io:println("onOpen: " + caller.getConnectionId());
//    }
//};
