/**
 * 
 */
package main.frameWork;

import java.util.HashMap;
import java.util.Map;

import main.frameWork.beans.AopsMapBean;
import main.frameWork.beans.MethodsWithObjs;
import main.frameWork.beans.ObjWithProxy;

public class Resources {
    public static Map<String, MethodsWithObjs> annotationMap = new HashMap<String, MethodsWithObjs>();
    public static Map<Class<?>, ObjWithProxy> beanMap;

    public static Map<Class<?>, AopsMapBean> aopsMap = new HashMap<>();

    public static String whereMainAt = "";
    public static String whereMainAtNoBin = "";
}
