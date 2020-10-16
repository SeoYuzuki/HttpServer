/**
 * 
 */
package HttpServer;

import java.util.HashMap;
import java.util.Map;

import HttpServer.beans.MethodsWithObjs;

public class Resources {
    public static Map<String, MethodsWithObjs> annotationMap = new HashMap<String, MethodsWithObjs>();
    public static Map<Class<?>, Object> beanMap = new HashMap<>();
    // public static ArrayList<Class<?>> aopsList = new ArrayList<>();
    public static Map<Class<?>, Object> aopsMap = new HashMap<>();
}
