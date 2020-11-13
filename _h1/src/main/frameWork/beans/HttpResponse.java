/**
 * 
 */
package main.frameWork.beans;

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

    public void setResponseString(String str) {
        setResponseData(str.getBytes());
    }

    Map<String, String> cookiesMap;

    public Map<String, String> getCookiesMap() {
        return cookiesMap;
    }

    public void setcookieMap(Map<String, String> map) {

        cookiesMap = map;
    }

    // for render
    private String realPath = null;
    private boolean isRenderMode = false;

    private List<String[]> list = null;
    private String renderWhat;

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

    public List<String[]> getList() {
        return list;
    }

    public boolean isWebSicket() {
        return isWebSicket;
    }

    public void setWebSicket(boolean isWebSicket) {
        this.isWebSicket = isWebSicket;
    }

}
