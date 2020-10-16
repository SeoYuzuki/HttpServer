/**
 * 
 */
package main.frameWork.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
    private boolean isWebSicket = false;
    private byte[] data;

    public byte[] getResponseData() {
        return data;
    }

    public void setResponseData(byte[] data) {
        this.data = data;
    }

    public void setResponseData(String str) {
        setResponseData(str.getBytes());
    }

    Map<String, String> cookiesMap;

    public Map<String, String> getCookiesMap() {
        return cookiesMap;
    }

    public void cookie(String k, String v) {
        if (cookiesMap == null) {
            cookiesMap = new HashMap<>();
        }
        cookiesMap.put(k, v);
    }

    // for render
    private String realPath = null;
    private boolean isRenderMode = false;

    private List<String[]> list = null;
    private String renderWhat;

    public HttpResponse renderHtml(String realPath) throws Exception {
        // System.out.println("--renderHtml--");
        if (this.realPath != null) {
            throw new Exception("已存在realPath");
        } else if (renderWhat != null) {
            throw new Exception("已存在renderWhat");
        }
        isRenderMode = true;
        renderWhat = "Html";
        this.realPath = realPath;
        list = new ArrayList<String[]>();
        return this;
    }

    public HttpResponse addData(String key, String value) throws Exception {
        if (this.realPath == null) {
            throw new Exception("不存在realPath");
        }
        String[] temp = {key, value };
        list.add(temp);
        return this;
    }

    public HttpResponse renderFile(String realPath) throws Exception {
        // System.out.println("--renderFile--");
        if (this.realPath != null) {
            throw new Exception("已存在realPath");
        } else if (renderWhat != null) {
            throw new Exception("已存在renderWhat");
        }
        isRenderMode = true;
        renderWhat = "File";
        this.realPath = realPath;
        return this;
    }

    /**
     * @return the renderWhat
     */
    public String getRenderWhat() {
        return renderWhat;
    }

    public String getRealPath() {
        return realPath;
    }

    /**
     * @return the isRenderMode
     */
    public boolean isRenderMode() {
        return isRenderMode;
    }

    /**
     * @return the list
     */
    public List<String[]> getList() {
        return list;
    }

    /**
     * @return the isWebSicket
     */
    public boolean isWebSicket() {
        return isWebSicket;
    }

    /**
     * @param isWebSicket the isWebSicket to set
     */
    public void setWebSicket(boolean isWebSicket) {
        this.isWebSicket = isWebSicket;
    }

}
