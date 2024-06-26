import ballerina/websocket;

service /foo on new websocket:Listener(9090) {
	resource function get .() returns websocket:Service|websocket:Error {
		return new WsService();
	}
}

service class WsService {
	*websocket:Service;

	remote function onMessage(websocket:Caller caller, anydata data) returns websocket:Error? {
	}
}
