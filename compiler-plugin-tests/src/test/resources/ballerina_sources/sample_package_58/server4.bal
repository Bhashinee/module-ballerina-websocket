// Copyright (c) 2023 WSO2 LLC. (www.wso2.com) All Rights Reserved.
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// //www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/websocket;

listener websocket:Listener localListener4 = new(8083);

@websocket:ServiceConfig {dispatcherKey: "type"}
service / on localListener4 {
   resource function get .() returns websocket:Service|error {
       return new WsService4();
   }
}

service class WsService4 {
    *websocket:Service;

    remote isolated function onMessage(json data) returns stream<string, error?>|stream<int, error?>|error {
        string[] greets = ["Hi Sam", "Hey Sam", "GM Sam"];
        return greets.toStream();
    }
}
