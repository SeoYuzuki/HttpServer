/**
 * 
 */
package main.frameWork.beans;

import java.lang.reflect.Method;

public class MethodsWithObjs {
    private Method realMethod;// main
    private Object realObj;

    private boolean isProxyed = false;
    private Method proxyMethod;
    private Object proxyObj;

    private boolean hadCheckAutowire = false;

    /**
     * @return the realMethod
     */
    public Method getRealMethod() {
        return realMethod;
    }

    /**
     * @param realMethod the realMethod to set
     */
    public void setRealMethod(Method realMethod) {
        this.realMethod = realMethod;
    }

    /**
     * @return the realObj
     */
    public Object getRealObj() {
        return realObj;
    }

    /**
     * @param realObj the realObj to set
     */
    public void setRealObj(Object realObj) {
        this.realObj = realObj;
    }

    /**
     * @return the isProxyed
     */
    public boolean isProxyed() {
        return isProxyed;
    }

    /**
     * @param isProxyed the isProxyed to set
     */
    public void setProxyed(boolean isProxyed) {
        this.isProxyed = isProxyed;
    }

    /**
     * @return the proxyMethod
     */
    public Method getProxyMethod() {
        return proxyMethod;
    }

    /**
     * @param proxyMethod the proxyMethod to set
     */
    public void setProxyMethod(Method proxyMethod) {
        this.proxyMethod = proxyMethod;
    }

    /**
     * @return the proxyObj
     */
    public Object getProxyObj() {
        return proxyObj;
    }

    /**
     * @param proxyObj the proxyObj to set
     */
    public void setProxyObj(Object proxyObj) {
        this.proxyObj = proxyObj;
    }

    /**
     * 
     * @return
     * @remark
     */
    public boolean hadCheckAutowire() {
        return hadCheckAutowire;
    }

    public void setHadCheckAutowire(boolean b) {
        hadCheckAutowire = b;
    }

}
