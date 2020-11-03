/**
 * 
 */
package main.frameWork.beans;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private String httpMethod;
    private String RequestURI;
    private String fullURL;
    private String rawHeader;
    private String rawParameter;
    private String rawPostBody;

    private Map<String, String> URLparameterMap;
    private Map<String, String> HttpHeaderMap;
    private Map<String, String> postBodyMap;

    private boolean isWebsocket;

    transient public DataOutputStream outToClient;
    transient public BufferedReader inFromClient;

    transient InputStream inputStream;
    transient OutputStream outputStream;

    public HttpRequest(String httpMethod, String rawURL,
            String rawHead, BufferedReader inFromClient, DataOutputStream outToClient) {
        this.httpMethod = httpMethod;
        this.rawHeader = rawHead;
        this.inFromClient = inFromClient;
        this.outToClient = outToClient;
        try {
            this.fullURL = URLDecoder.decode(rawURL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // System.out.println("--fill--");
        // HttpRequest htmlRequest = new HttpRequest(httpMethod, URLDecoder.decode(rawURL, "UTF-8"));
        // System.out.println("fullURL:" + htmlRequest.getFullURL());

        String requestURI = this.getFullURL().split("\\?")[0];
        this.setRequestURI(requestURI);

        if (this.getFullURL().split("\\?").length > 1) {
            this.rawParameter = this.getFullURL().split("\\?")[1];
            Map<String, String> parameterMap = rawStringToMap(rawParameter);
            this.setParameterMap(parameterMap);
        }

        // 填入Header資訊
        HttpHeaderMap = new HashMap<String, String>();
        String[] temp = null;
        for (String ss : rawHead.split("\r\n\r\n")[0].split("\r\n")) {
            temp = ss.split(": ");
            if (temp.length >= 2) {
                HttpHeaderMap.put(ss.split(": ")[0], ss.split(": ")[1]);
            }
        }

        // 判斷websocket
        if (HttpHeaderMap.containsKey("Upgrade")) {
            isWebsocket = true;
        }

        // 處理POST BODY
        if (rawHead.split("\r\n\r\n").length > 1) {
            rawPostBody = rawHead.split("\r\n\r\n")[1];
            postBodyMap = rawStringToMap(rawPostBody);
        } else if (HttpHeaderMap.containsKey("Content-Length")) {
            if (Integer.parseInt(HttpHeaderMap.get("Content-Length")) > 0) {
                String s = "";
                try {
                    s = s + (char) inFromClient.read();
                    // inFromClient.reset();
                    // System.out.println("s:" + s);
                    while (s.length() < Integer.parseInt(HttpHeaderMap.get("Content-Length"))) {
                        System.out.println("s.length()" + s.length());
                        s = s + (char) inFromClient.read();
                    }
                    System.out.println(s);
                    postBodyMap = rawStringToMap(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /**
     * 處理 name=ae&passwd=ww 變成Map {passwd=ww, name=ae}
     */
    private HashMap<String, String> rawStringToMap(String rawString) {

        return rawStringToMap(new HashMap<String, String>(), rawString);
    }

    /**
     * 處理 name=ae&passwd=ww 變成Map {passwd=ww, name=ae} <br>
     * return null if error
     */
    private HashMap<String, String> rawStringToMap(HashMap<String, String> map, String rawString) {
        try {
            for (String str : rawString.split("&")) {
                map.put(str.split("=")[0], str.split("=")[1]);
            }
            return map;
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * @return the isWebsocket
     */
    public boolean isWebsocket() {
        return isWebsocket;
    }

    /**
     * @param isWebsocket the isWebsocket to set
     */
    public void setWebsocket(boolean isWebsocket) {
        this.isWebsocket = isWebsocket;
    }

    /**
     * @return the outToClient
     */
    public DataOutputStream getOutToClient() {
        return outToClient;
    }

    /**
     * @param outToClient the outToClient to set
     */
    public void setOutToClient(DataOutputStream outToClient) {
        this.outToClient = outToClient;
    }

    /**
     * @return the inFromClient
     */
    public BufferedReader getInFromClient() {
        return inFromClient;
    }

    /**
     * @param inFromClient the inFromClient to set
     */
    public void setInFromClient(BufferedReader inFromClient) {
        this.inFromClient = inFromClient;
    }

    /**
     * Returns the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request. The web container does not decode this String. For example: First
     * line of HTTP request Returned Value <br>
     * POST /some/path.html HTTP/1.1 /some/path.html <br>
     * GET http://foo.bar/a.html HTTP/1.0 /a.html <br>
     * HEAD /xyz?a=b HTTP/1.1 /xyz
     */
    public String getRequestURI() {
        return RequestURI;
    }

    public void setRequestURI(String requestURI) {
        RequestURI = requestURI;
    }

    public Map<String, String> getURLParameterMap() {
        return URLparameterMap;
    }

    public String getFullURL() {
        return fullURL;
    }

    public void setFullURL(String fullURL) {
        this.fullURL = fullURL;
    }

    public String getRawParameter() {
        return rawParameter;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.URLparameterMap = parameterMap;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRawHeader() {
        return rawHeader;
    }

    public Map<String, String> getPostBodyMap() {
        return postBodyMap;
    }

    /**
     * @param string
     * @return
     * @remark
     */
    public Map<String, String> getHttpHeaderMap() {

        return HttpHeaderMap;
    }

    /**
     * @param inputStream
     * @remark
     */

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;

    }

    public OutputStream getOutputStream() {
        return outputStream;

    }

    public String getRawPostBody() {
        return rawPostBody;
    }

}