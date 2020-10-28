package main.frameWork.beans;

import java.lang.reflect.Method;

public class AopsMapBean {

    private Object aopObj = null;
    private String JsEmbeddedPath = "";
    private Method AopOnAfterMethod = null;
    private Method AopOnBeforeMethod = null;
    private Method AopOnErrorMethod = null;
    private boolean doAfterError;

    public void setAopOnErrorMethod(Method aopOnErrorMethod) {
        AopOnErrorMethod = aopOnErrorMethod;
    }

    public void setAopObj(Object aopObj) {
        this.aopObj = aopObj;
    }

    public Object getAopObj() {
        return aopObj;
    }

    public String getJsEmbeddedPath() {
        return JsEmbeddedPath;
    }

    public Method getAopOnAfterMethod() {
        return AopOnAfterMethod;
    }

    public Method getAopOnBeforeMethod() {
        return AopOnBeforeMethod;
    }

    public void setJsEmbeddedPath(String str) {
        JsEmbeddedPath = str;
    }

    public void setAopOnAfterMethod(Method mm) {
        AopOnAfterMethod = mm;
    }

    public void setAopOnBeforeMethod(Method mm) {
        AopOnBeforeMethod = mm;
    }

    public Method getAopOnErrorMethod() {
        return AopOnErrorMethod;
    }

    public void doAfterError(boolean b) {
        doAfterError = b;

    }

    public boolean isDoAfterError() {
        return doAfterError;
    }
}
