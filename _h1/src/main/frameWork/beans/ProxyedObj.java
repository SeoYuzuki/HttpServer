
package main.frameWork.beans;

/**
 * for aop
 */
public class ProxyedObj {
    final private Object realObject;
    final private Object proxyObject;

    public ProxyedObj(Object realObject, Object proxyObject) {
        this.realObject = realObject;
        this.proxyObject = proxyObject;
    }

    public Object getProxyObject() {
        return proxyObject;
    }

    public Object getRealObject() {
        return realObject;
    }

}