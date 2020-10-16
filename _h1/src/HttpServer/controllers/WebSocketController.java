/**
 * 
 */
package HttpServer.controllers;

import HttpServer.annotations.WsOnMessage;
import HttpServer.annotations.WsOnOpen;
import HttpServer.annotations.WsOnClose;
import HttpServer.annotations.WsServerEndpoint;
import HttpServer.beans.HttpRequest;

@WsServerEndpoint()
public class WebSocketController {

    @WsOnOpen
    public String onOpen(HttpRequest req) {
        System.out.println("@WsOnOpen : " + req.getHttpHeaderMap());
        return "okok";
    }

    @WsOnMessage
    public String onMessage(String message) {
        System.out.println("@WsOnMessage1 : " + message);
        return "okok";
    }

    @WsOnMessage(TypeOfFrame = "binary")
    public String onMessage2(byte[] message) {
        System.out.println("@WsOnMessage2 : " + message);
        System.out.println("@WsOnMessage2 : " + new String(message));
        return "okok";
    }

    @WsOnClose
    public String onClose(String message) {
        System.out.println("@onClose : " + message);
        return "okok";
    }
}
