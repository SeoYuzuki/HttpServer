/**
 * 
 */
package main.frameWork;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import main.frameWork.beans.AopsMapBean;
import main.frameWork.beans.MethodsWithObjs;
import main.frameWork.beans.ObjWithProxy;

public class Resources {
    public static Map<String, MethodsWithObjs> annotationMap = new ConcurrentHashMap<String, MethodsWithObjs>();
    public static Map<Class<?>, ObjWithProxy> beanMap;

    public static Map<Class<?>, AopsMapBean> aopsMap = new ConcurrentHashMap<>();

    public static String whereMainAt = "";
    public static String whereMainAtNoBin = "";
}
