/**
 * 
 */
package main.frameWork.beans;

public class ObjectWithProxy {
    private Object realObject;
    private Object proxyObject;

    /**
     * @return the realObject
     */
    public Object getRealObject() {
        return realObject;
    }

    /**
     * @param realObject the realObject to set
     */
    public void setRealObject(Object realObject) {
        this.realObject = realObject;
    }

    /**
     * @return the proxyObject
     */
    public Object getProxyObject() {
        return proxyObject;
    }

    /**
     * @param proxyObject the proxyObject to set
     */
    public void setProxyObject(Object proxyObject) {
        this.proxyObject = proxyObject;
    }
}