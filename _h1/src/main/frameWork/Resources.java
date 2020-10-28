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
    public final static Map<String, MethodsWithObjs> annotationMap = new HashMap<String, MethodsWithObjs>();
    public final static Map<Class<?>, ObjWithProxy> beanMap = new HashMap<>();;

    public final static Map<Class<?>, AopsMapBean> aopsMap = new HashMap<>();

    public static volatile String whereMainAt = "";
    public static volatile String whereMainAtNoBin = "";
}
