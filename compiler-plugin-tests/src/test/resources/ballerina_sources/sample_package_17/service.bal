import ballerina/websocket as ws;

listener ws:Listener hl = check new(21001);

service /basic/ws on hl {
   resource isolated function get abc() returns ws:Service|ws:UpgradeError {
       return new WsService();
   }
}

service isolated class WsService {
    *ws:Service;
    remote function onOpen(ws:Caller caller) returns ws:Error? {
        return ();
    }

    remote function onClose(string message, int status) returns ws:Error? {
        return ();
    }
}
