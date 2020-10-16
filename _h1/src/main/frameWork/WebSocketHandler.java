/**
 * 
 */
package main.frameWork;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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

    WebSocketHandler(HttpRequest req, Map<String, MethodsWithObjs> annotationMap) {
        this.req = req;
        this.annotationMap = annotationMap;
    }

    public void all() throws Exception {

        handleHttpHeader();
        toOnOpen(req);
        String endStr = doWs();
        toOnClose(endStr);
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

    private void toOnOpen(HttpRequest req) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        MethodsWithObjs methodObj = annotationMap.get("WsOnOpen");
        methodObj.getRealMethod().invoke(methodObj.getRealObj(), req);

    }

    private String doWs() throws Exception {

        String endStr = readFrameOrWait(req);

        return endStr;
    }

    private String readFrameOrWait(HttpRequest req) throws Exception {
        InputStream in = req.getInputStream();

        byte[] frameBuf = new byte[128];

        while (true) {
            try {

                int rDataEnd = in.read(frameBuf);
                Frame frame = null;
                if (rDataEnd != -1) {
                    frame = new Frame(frameBuf, rDataEnd); // new frame一個並且解析
                    frame.show(false);// 是否顯示byte

                    while (frame.getLeftDataToSendLength() > 0) {// 若資料多到第一個buf讀不完,繼續讀
                        rDataEnd = in.read(frameBuf);
                        frame.readData(frameBuf, rDataEnd);

                        if (frame.getLeftDataToSendLength() < 0) {
                            throw new Exception("非期待的LeftDataToSendLength");
                        }
                    }
                }

                if (frame.getOpcode() == 0x01) {
                    String reStr = toOnMessage(frame.getByteArrayOutputStream().toByteArray(), false);

                    brodcast(reStr, 129);
                } else if (frame.getOpcode() == 0x02) {
                    String reStr = toOnMessage(frame.getByteArrayOutputStream().toByteArray(), true);
                    brodcast(reStr, 129);
                } else if (frame.getOpcode() == 0x08) {
                    toOnClose(new String(frame.getByteArrayOutputStream().toByteArray()));
                    brodcast("", 136);

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
            methodObj = annotationMap.get("WsOnMessage_binary");
            if (methodObj == null) {
                throw new Exception("get binary message but no corresponding OnMessage annotation");
            }
            reStr = (String) methodObj.getRealMethod().invoke(
                    methodObj.getRealObj(), obj);
        } else {
            methodObj = annotationMap.get("WsOnMessage_text");
            if (methodObj == null) {
                throw new Exception("get text message but no corresponding OnMessage annotation");

            }
            reStr = (String) methodObj.getRealMethod().invoke(
                    methodObj.getRealObj(), new String(obj));
        }
        return reStr;

    }

    /**
     * 129 10000001 text <br>
     * 130 10000002 binary <br>
     * 136 10001000 close
     * 
     */
    private void brodcast(String mess, int frame0code) throws IOException {
        OutputStream out = req.getOutputStream();
        byte[] rawData = mess.getBytes();

        int frameCount = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) frame0code;

        if (rawData.length <= 125) {
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        } else if (rawData.length >= 126 && rawData.length <= 65535) {
            frame[1] = (byte) 126;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 8) & (byte) 255);
            frame[3] = (byte) (len & (byte) 255);
            frameCount = 4;
        } else {
            frame[1] = (byte) 127;
            int len = rawData.length;
            frame[2] = (byte) ((len >> 56) & (byte) 255);
            frame[3] = (byte) ((len >> 48) & (byte) 255);
            frame[4] = (byte) ((len >> 40) & (byte) 255);
            frame[5] = (byte) ((len >> 32) & (byte) 255);
            frame[6] = (byte) ((len >> 24) & (byte) 255);
            frame[7] = (byte) ((len >> 16) & (byte) 255);
            frame[8] = (byte) ((len >> 8) & (byte) 255);
            frame[9] = (byte) (len & (byte) 255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        byte[] reply = new byte[bLength];

        int bLim = 0;
        for (int i = 0; i < frameCount; i++) {
            reply[bLim] = frame[i];
            bLim++;
        }
        for (int i = 0; i < rawData.length; i++) {
            reply[bLim] = rawData[i];
            bLim++;
        }

        out.write(reply);
        out.flush();

    }

    private void toOnClose(String str) throws Exception {
        MethodsWithObjs methodObj = annotationMap.get("WsOnClose");
        methodObj.getRealMethod().invoke(methodObj.getRealObj(), str);
    }

}
