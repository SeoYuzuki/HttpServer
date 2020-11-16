package main.frameWork.beans;

import java.util.HashMap;
import java.util.Map;

public class BeanResource {
    private final Map<String, MethodsWithObjs> annotationMap = new HashMap<String, MethodsWithObjs>();
    private final Map<Class<?>, ObjWithProxy> beanMap = new HashMap<>();;

    private final Map<Class<?>, AdviceBean> AdvicesMap = new HashMap<>();

    private final ThreadLocal<Object> currentProxy = new ThreadLocal<>();

    private volatile String whereMainAt = "";
    private volatile String whereMainAtNoBin = "";

    /**
     * @return the whereMainAt
     */
    public String getWhereMainAt() {
        return whereMainAt;
    }

    /**
     * @param whereMainAt the whereMainAt to set
     */
    public void setWhereMainAt(String whereMainAt) {
        this.whereMainAt = whereMainAt;
    }

    /**
     * @return the whereMainAtNoBin
     */
    public String getWhereMainAtNoBin() {
        return whereMainAtNoBin;
    }

    /**
     * @param whereMainAtNoBin the whereMainAtNoBin to set
     */
    public void setWhereMainAtNoBin(String whereMainAtNoBin) {
        this.whereMainAtNoBin = whereMainAtNoBin;
    }

    /**
     * @return the annotationMap
     */
    public Map<String, MethodsWithObjs> getAnnotationMap() {
        return annotationMap;
    }

    /**
     * @return the beanMap
     */
    public Map<Class<?>, ObjWithProxy> getBeanMap() {
        return beanMap;
    }

    /**
     * @return the advicesMap
     */
    public Map<Class<?>, AdviceBean> getAdvicesMap() {
        return AdvicesMap;
    }

    /**
     * @return the currentProxy
     */
    public ThreadLocal<Object> getCurrentProxy() {
        return currentProxy;
    }

}
