
package main.frameWork.beans;

/**
 * for aop
 */
public class ObjWithProxy {
    final private Object realObject;
    final private Object proxy;

    public ObjWithProxy(Object realObject, Object proxyObject) {
        this.realObject = realObject;
        this.proxy = proxyObject;
    }

    public Object getProxy() {
        return proxy;
    }

    public Object getRealObject() {
        return realObject;
    }

}