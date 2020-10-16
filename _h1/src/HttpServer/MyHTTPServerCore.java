package HttpServer;

import java.io.*;
import java.net.*;
import java.util.*;

import HttpServer.beans.HttpRequest;
import HttpServer.beans.HttpResponse;
import HttpServer.beans.MethodsWithObjs;

public class MyHTTPServerCore extends Thread {

    private Socket connectedClient = null;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;

    public MyHTTPServerCore(Socket client) {
        connectedClient = client;

    }

    public void run() {
        try {

            // System.out.println("The Client " + connectedClient.getInetAddress()
            // + ":" + connectedClient.getPort() + " is connected");

            inFromClient = new BufferedReader(new InputStreamReader(connectedClient.getInputStream()));
            outToClient = new DataOutputStream(connectedClient.getOutputStream());

            String requestString = inFromClient.readLine();
            System.out.println("-----------------");
            System.out.println("requestString: " + requestString);
            String headerLine = requestString;
            if (headerLine == null) {
                System.out.println("headerLine null");
                System.out.println("!!" + inFromClient.read());
                return;
            }
            StringTokenizer tokenizer = new StringTokenizer(headerLine);
            String httpMethod = tokenizer.nextToken();
            String rawURL = tokenizer.nextToken();

            String rawHead = "";

            while (inFromClient.ready()) {
                // Read the HTTP complete HTTP Query
                rawHead = rawHead + (char) inFromClient.read();
            }

            // System.out.println("t3: " + rawHead);
            // 填寫欄位的邏輯寫在HttpRequest建構子裡
            HttpRequest htmlRequest = new HttpRequest(httpMethod, rawURL, rawHead, inFromClient, outToClient);
            htmlRequest.setInputStream(connectedClient.getInputStream());
            htmlRequest.setOutputStream(connectedClient.getOutputStream());
            HttpResponse httpResponse = new HttpResponse();
            invokeToController(htmlRequest, httpResponse);

            handleResponse(httpResponse);

        } catch (MyHTTPException e) {
            System.out.println("MyHttpException " + e.getMessage());
            String mes = e.getMessage();

            try {
                sendEazyResponse_(404, mes);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            closeSocket();
            System.out.println("finally");
            // closeSocket();
        }
    }

    private void handleResponse(HttpResponse httpResponse) throws Exception {
        byte[] data = null;
        if (!httpResponse.isRenderMode()) {
            data = httpResponse.getResponseData();
        } else if (httpResponse.isRenderMode()) {
            data = new RenderModel().toRender(httpResponse);
        }
        if (!httpResponse.isWebSicket()) {
            writehttpResponse1(httpResponse.getCookiesMap());
            writehttpResponse2(data);
        } else {

        }

    }

    public void invokeToController(HttpRequest htmlRequest, HttpResponse httpResponse) throws Exception {

        if (htmlRequest.isWebsocket()) {// http + websocket
            new WebSocketHandler(htmlRequest, Resources.annotationMap).all();
            httpResponse.setWebSicket(true);
        } else {// http
            MethodsWithObjs methodObj = getMethodbyAnnotation(htmlRequest);
            if (methodObj == null) {

                throw new MyHTTPException("invokeToController: get no method to invoke, check route");
            }

            if (methodObj.isProxyed()) {
                Object proxyObj = methodObj.getProxyObj();
                methodObj.getProxyMethod().invoke(proxyObj, htmlRequest, httpResponse);
            } else {
                methodObj.getRealMethod().invoke(methodObj.getRealObj(), htmlRequest, httpResponse);
            }

        }

    }

    private MethodsWithObjs getMethodbyAnnotation(HttpRequest htmlRequest) throws Exception {
        // System.out.println("!!!!" + htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());
        // MethodwithInvokeObj aa = annotationMap.get(htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());
        MethodsWithObjs methodsWithObjs = Resources.annotationMap.get(htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());

        if (methodsWithObjs != null) {
            return methodsWithObjs;
        } else {

            if (htmlRequest.getHttpHeaderMap().get("Sec-Fetch-Dest").startsWith("image")) {
                return Resources.annotationMap.get("GET_file");

            } else {
                return null;
            }
        }

    }

    private void writehttpResponse1(Map<String, String> map) throws Exception {
        String statusLine = "HTTP/1.1 200 OK" + "\r\n";
        String serverdetails = "Server: Java HTTPServer";
        String content_Type = "Content-Type: text/html" + "\r\n";
        // String contentLengthLine = "";

        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(content_Type);
        if (map != null) {
            map.forEach((k, v) -> {
                try {
                    outToClient.writeBytes("Set-Cookie: " + k + "=" + v + "\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private void writehttpResponse2(byte[] data) throws Exception {

        int dataLength = 0;
        if (data != null) {
            dataLength = data.length;
            outToClient.writeBytes("Content-Length: " + Integer.toString(dataLength) + "\r\n");
            outToClient.writeBytes("Connection: close\r\n");
            outToClient.writeBytes("\r\n");
            outToClient.write(data);
            outToClient.close();
        } else {
            throw new MyHTTPException("data NUll");
        }

    }

    public void sendEazyResponse_(int statusCode, String responseString) throws Exception {

        String statusLine = "HTTP/1.1 404 Not Found" + "\r\n";
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
        String contentTypeLine = "Content-Type: text/html" + "\r\n";

        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(contentTypeLine);
        outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");
        outToClient.writeBytes(responseString);

        outToClient.close();

    }

    private void closeSocket() {
        try {
            outToClient.close();
            inFromClient.close();
            connectedClient.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
