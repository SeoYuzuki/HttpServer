package main.frameWork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderBean {
    private String type = "";
    private byte[] data;
    private String path = "";

    private List<String[]> transList = new ArrayList<String[]>();;
    private Map<String, String> cookiesMap = new HashMap<>();;

    public RenderBean(String type) {
        this.type = type;
    }

    public RenderBean setByte(byte[] data) {
        this.data = data;
        return this;
    }

    public RenderBean path(String path) {
        this.path = path;
        return this;
    }

    public RenderBean trans(String key, String value) {
        String[] temp = {key, value };
        transList.add(temp);
        return this;
    }

    public RenderBean addCookie(String key, String value) {
        cookiesMap.put(key, value);
        return this;
    }

    public String getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public String getPath() {
        return path;
    }

    public List<String[]> getTransList() {
        return transList;
    }

    public Map<String, String> getCookiesMap() {
        return cookiesMap;
    }
}
