/**
 * 
 */
package main.frameWork;

import java.util.HashMap;
import java.util.Map;

import main.frameWork.beans.MethodsWithObjs;
import main.frameWork.beans.ProxyedObj;

public class Resources {
    public static Map<String, MethodsWithObjs> annotationMap = new HashMap<String, MethodsWithObjs>();
    public static Map<Class<?>, ProxyedObj> beanMap;
    // public static ArrayList<Class<?>> aopsList = new ArrayList<>();
    public static Map<Class<?>, Object> aopsMap = new HashMap<>();

    public static String whereMainAt = "";
    public static String whereMainAtNoBin = "";
}
