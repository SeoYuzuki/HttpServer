/**
 * 
 */
package main.controller;

import main.frameWork.annotatoins.WsOnClose;
import main.frameWork.annotatoins.WsOnMessage;
import main.frameWork.annotatoins.WsOnOpen;
import main.frameWork.annotatoins.WsServerEndpoint;
import main.frameWork.beans.HttpRequest;

@WsServerEndpoint(route = "/w2")
public class WebSocketController2 {

    @WsOnOpen
    public String onOpen(HttpRequest req) {
        System.out.println("@WsOnOpen : " + req.getHttpHeaderMap());
        return "okok2";
    }

    @WsOnMessage
    public String onMessage(String message) {
        System.out.println("@WsOnMessage1 : " + message);
        return "okok2";
    }

    @WsOnMessage(TypeOfFrame = "binary")
    public String onMessage2(byte[] message) {
        System.out.println("@WsOnMessage2 : " + message);
        System.out.println("@WsOnMessage2 : " + new String(message));
        return "okok2";
    }

    @WsOnClose
    public String onClose(String message) {
        System.out.println("@onClose : " + message);
        return "okok2";
    }
}
