package main.frameWork;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import main.frameWork.beans.Frame;
import main.frameWork.beans.HttpRequest;
import main.frameWork.beans.MethodsWithObjs;

public class WebSocketHandler {
    HttpRequest req;
    boolean isShow = false;
    Map<String, MethodsWithObjs> annotationMap;
    private String classRoute = "";

    WebSocketHandler(HttpRequest req, Map<String, MethodsWithObjs> annotationMap, String classRoute) {
        this.req = req;
        this.annotationMap = annotationMap;
        this.classRoute = classRoute;
    }

    public void all() throws Exception {
        try {
            handleHttpHeader();
            toOnOpen(req);
            String endStr = doWs();
            toOnClose(endStr);
        } catch (MyHTTPException mye) {
            mye.isWebsocket(true);
            throw mye;
        }

    }

    private void handleHttpHeader() throws Exception {
        System.out.println("--handleHttpHeader--");

        DataOutputStream outToClient = req.outToClient;
        String key = req.getHttpHeaderMap().get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(key.getBytes());// DigestUtils.sha1(key);

        String encodedText = Base64.getEncoder().encodeToString(sha1);
        // System.out.println("Sec-WebSocket-Accept: " + encodedText);

        outToClient.writeBytes("HTTP/1.1 101 Switching Protocols" + "\r\n");

        outToClient.writeBytes("Sec-WebSocket-Origin: " + "http://127.0.0.1:5000" + "\r\n");
        // outToClient.writeBytes("Sec-WebSocket-Extensions: permessage-deflate; client_max_window_bits");

        outToClient.writeBytes("Upgrade: Websocket" + "\r\n");
        outToClient.writeBytes("Sec-WebSocket-Accept: " + encodedText + "\r\n");
        outToClient.writeBytes("Server: Java HTTPServer");
        outToClient.writeBytes("Content-Length: " + Integer.toString(0) + "\r\n");
        outToClient.writeBytes("Connection: Upgrade\r\n");
        outToClient.writeBytes("Sec-WebSocket-Protocol: my-custom-protocol" + "\r\n");
        outToClient.writeBytes("\r\n");

        System.out.println("--handleHttpHeader end--");

    }

    private void toOnOpen(HttpRequest req) throws Exception {
        MethodsWithObjs methodObj = annotationMap.get(classRoute + "#WsOnOpen");

        if (methodObj == null) {

            throw new MyHTTPException("cannot find valid path for websocket open");
        }

        methodObj.getRealMethod().invoke(methodObj.getRealObj(), req);

    }

    private String doWs() throws Exception {

        String endStr = readFrameOrWait(req);

        return endStr;
    }

    private String readFrameOrWait(HttpRequest req) throws Exception {
        InputStream in = req.getInputStream();
        OutputStream out = req.getOutputStream();

        byte[] frameBuf = new byte[128];

        while (true) {
            try {
                FrameHandler frameHandler = new FrameHandler();
                Frame frame = frameHandler.parse(in, frameBuf);

                if (frame.getOpcode() == 0x01) {
                    String reStr = toOnMessage(frame.getByteArrayOutputStream().toByteArray(), false);

                    out.write(frameHandler.createReplyByte(reStr, 0x01 | 0X80));
                    out.flush();
                } else if (frame.getOpcode() == 0x02) {
                    String reStr = toOnMessage(frame.getByteArrayOutputStream().toByteArray(), true);

                    out.write(frameHandler.createReplyByte(reStr, 0x02 | 0X80));
                    out.flush();

                } else if (frame.getOpcode() == 0x08) {
                    toOnClose(new String(frame.getByteArrayOutputStream().toByteArray()));

                    out.write(frameHandler.createReplyByte("", 0x08 | 0X80));
                    out.flush();

                    break;
                } else {
                    throw new Exception("Opcode not support");
                }

            } catch (SocketException e) {
                // e.printStackTrace();
                System.out.println("SocketException!!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        return "";

    }

    private String toOnMessage(byte[] obj, boolean isBinary) throws Exception {
        MethodsWithObjs methodObj = null;
        String reStr;

        if (isBinary) {
            methodObj = annotationMap.get(classRoute + "#WsOnMessage_binary");
            if (methodObj == null) {
                throw new Exception("get binary message but no corresponding OnMessage annotation");
            }
            reStr = (String) methodObj.getRealMethod().invoke(
                    methodObj.getRealObj(), obj);
        } else {
            methodObj = annotationMap.get(classRoute + "#WsOnMessage_text");
            if (methodObj == null) {
                throw new Exception("get text message but no corresponding OnMessage annotation");

            }
            reStr = (String) methodObj.getRealMethod().invoke(
                    methodObj.getRealObj(), new String(obj));
        }
        return reStr;

    }

    private void toOnClose(String str) throws Exception {
        MethodsWithObjs methodObj = annotationMap.get(classRoute + "#WsOnClose");
        methodObj.getRealMethod().invoke(methodObj.getRealObj(), str);
    }

}