/**
 * 
 */
package main.frameWork;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;
import main.frameWork.annotatoins.WebPath;
import main.frameWork.annotatoins.WsOnClose;
import main.frameWork.annotatoins.WsOnMessage;
import main.frameWork.annotatoins.WsOnOpen;
import main.frameWork.beans.MethodsWithObjs;
import main.frameWork.beans.Object_proxy;
import main.frameWork.interfaces.CustomedAOP;

public class ReflectionsUtil {
    private ArrayList<MethodsWithObjs> arr;
    private static Map<String, MethodsWithObjs> annotationMap;

    /**
     * 依照 annotation Controller.class 和 WebPath.class 篩選method存入map
     * @param annotationMap
     * @throws Exception
     * @remark
     */
    static public void ScanClassToAnnotationMap() {
        try {
            annotationMap = Resources.annotationMap;
            new ReflectionsUtil().ScanClassToAnnotationMap_noLibrary();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReflectionsUtil() {
        arr = new ArrayList<>();
    }

    /**
     * 
     * @param annotationMap
     * @remark
     */
    private void ScanClassToAnnotationMap_noLibrary() {
        try {

            //
            String applicationPath = this.getClass().getResource("/").getPath().substring(1);
            Files.walk(Paths.get(applicationPath))
                    .filter(Files::isRegularFile)
                    .filter(classFile -> {
                        if (classFile.getParent().toString().replaceAll("\\\\", "/").split(applicationPath).length > 1) {
                            return classFile.getFileName().toString().endsWith(".class");
                        } else {
                            return false;
                        }
                    })
                    .map(classFile -> {
                        String packagePath = classFile.getParent().toString().replaceAll("\\\\", "/").split(applicationPath)[1].replace("/", ".");
                        String className = classFile.getFileName().toString().split("\\.")[0];
                        return String.format("%s.%s", packagePath, className);
                    })
                    .forEach(fqcn -> {
                        try {
                            Class<?> loopClass = Class.forName(fqcn);
                            if (loopClass.isAnnotation() == false) {
                                if (isAnnotaionExtend(loopClass, Context.class)) {// Annotation 繼承判斷
                                    addMethodsWithObjsToList(loopClass);
                                }

                                // System.out.println("XX: " + loopClass);
                                for (Class<?> cc : loopClass.getInterfaces()) {
                                    if (cc == CustomedAOP.class) {
                                        Resources.aopsMap.put(loopClass, loopClass.newInstance());
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            doController();
            doWS();

            doAutoWired();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Map<Class<?>, Object_proxy> beanMap = new HashMap<>();// for AutoWired

    private void doAutoWired() throws Exception {

        for (Entry<Class<?>, Object_proxy> en : beanMap.entrySet()) {
            // System.out.println("!!" + en.getValue().realObject.getClass());
            for (Field fi : en.getValue().getRealObject().getClass().getFields()) {
                // System.out.println(fi.getName());

                if (fi.getAnnotation(Autowired.class) != null) {
                    // System.out.println(fi.getName() + " / " + fi.getType());

                    if (fi.getType().isInterface()) {
                        fi.set(en.getValue().getRealObject(), beanMap.get(fi.getType()).getProxyObject());
                    } else {
                        fi.set(en.getValue().getRealObject(), beanMap.get(fi.getType()).getProxyObject());
                    }

                }
            }

        }
    }

    private void addMethodsWithObjsToList(Class<?> loopClass) {
        try {
            Constructor<?> constructor = loopClass.getDeclaredConstructors()[0];
            Object realObj = constructor.newInstance();

            Object proxy = new SimpleProxyHandler().bind(realObj);// 製作proxy物件
            Object_proxy op = new Object_proxy();
            op.setRealObject(realObj);
            op.setProxyObject(proxy);
            System.out.println(loopClass.getName());
            if (op.getRealObject().getClass().getInterfaces().length > 0) {
                beanMap.put(op.getRealObject().getClass().getInterfaces()[0], op);
            } else {
                beanMap.put(loopClass, op);
            }
            for (int i = 0; i < loopClass.getMethods().length; i++) {
                MethodsWithObjs mObj = new MethodsWithObjs();
                mObj.setRealObj(realObj);

                // method層級
                Method mm = loopClass.getMethods()[i];
                mObj.setRealMethod(mm);
                // if method或class有壓AOP
                if (mm.getAnnotation(AOP.class) != null || loopClass.getAnnotation(AOP.class) != null) {
                    mObj.setProxyed(true);
                    mObj.setProxyObj(proxy);

                    for (Method proxyM : proxy.getClass().getMethods()) {
                        if (proxyM.getName().equals(mm.getName())) {
                            mObj.setProxyMethod(proxyM);
                            break;
                        }
                    }
                    if (mObj.getProxyMethod() == null) {
                        throw new Exception("ProxyMethod不該為null");
                    }
                }

                arr.add(mObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doController() throws MyHTTPException {
        for (MethodsWithObjs mm : arr) {
            WebPath anno = mm.getRealMethod().getAnnotation(WebPath.class);
            if (anno != null) {
                if (annotationMap.containsKey(anno.methed() + "_" + anno.route())) {
                    throw new MyHTTPException("duplicate route by WebPath");
                }
                annotationMap.put(anno.methed() + "_" + anno.route(), mm);
            }
        }
    }

    private void doWS() throws MyHTTPException {
        for (MethodsWithObjs mm : arr) {

            WsOnOpen wsOnOpen = mm.getRealMethod().getAnnotation(WsOnOpen.class);
            if (wsOnOpen != null) {
                if (annotationMap.containsKey("WsOnOpen")) {
                    throw new MyHTTPException("duplicate route by WsOnOpen");
                }
                annotationMap.put("WsOnOpen", mm);
                continue;
            }
            WsOnMessage wsOnMessage = mm.getRealMethod().getAnnotation(WsOnMessage.class);
            if (wsOnMessage != null) {
                if (annotationMap.containsKey("WsOnMessage_" + wsOnMessage.TypeOfFrame())) {
                    throw new MyHTTPException("duplicate route by wsOnMessage");
                }
                annotationMap.put("WsOnMessage_" + wsOnMessage.TypeOfFrame(), mm);
                continue;
            }
            WsOnClose wsOnClose = mm.getRealMethod().getAnnotation(WsOnClose.class);
            if (wsOnClose != null) {
                if (annotationMap.containsKey("WsOnClose")) {
                    throw new MyHTTPException("duplicate route by WsOnClose");
                }
                annotationMap.put("WsOnClose", mm);
                continue;
            }

        }
    }

    /**
     * 是否class 的annotation (包含其annotation的annotation), 有存在
     */
    private boolean isAnnotaionExtend(Class<?> loopClass, Class clazz) {
        return isAnnotaionExtend(loopClass, clazz, new ArrayList<Class<?>>());
    }

    /**
     * 是否class 的annotation (包含其annotation的annotation), 有存在
     */
    private boolean isAnnotaionExtend(Class<?> loopClass, Class clazz, List<Class<?>> list) {
        list.add(loopClass); // 避免annototaion壓重複的annototaion,造成無窮疊帶, 所以添加入list中,已經包含的就不跑
        if (loopClass.getAnnotation(clazz) != null) {
            list.remove(loopClass);
            return true;
        }
        boolean temp = false;
        for (Annotation anno : loopClass.getAnnotations()) {
            if (!list.contains(anno.annotationType())) {// 已經包含的就不跑
                temp = isAnnotaionExtend(anno.annotationType(), clazz, list);
                if (temp == true) {
                    break;
                }
            }
        }
        list.remove(loopClass);
        return temp;
    }

    /**
     * 使用 org.reflections.Reflections
     * @param annotationMap
     * @throws Exception
     * @remark
     */
    // private void ScanClassToAnnotationMap_O(Map<String, MethodwithInvokeObj> annotationMap) throws Exception {
    // List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
    // classLoadersList.add(ClasspathHelper.contextClassLoader());
    // classLoadersList.add(ClasspathHelper.staticClassLoader());
    //
    // Reflections reflections = new Reflections(new ConfigurationBuilder()
    // .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
    // .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
    // .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("HttpServer"))));
    // Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
    //
    // for (Class<?> clazz : classes) {
    // if (clazz.isAnnotationPresent(Controller.class)) {
    // Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
    //
    // Object constructor_newInstance = constructor.newInstance();
    //
    // for (Method method : clazz.getDeclaredMethods()) {
    // WebPath annotation = method.getAnnotation(WebPath.class);
    // if (annotation != null) {
    // MethodwithInvokeObj aa = new MethodwithInvokeObj(
    // constructor_newInstance, method, annotation.methed(), annotation.route());
    //
    // if (annotationMap.containsKey(annotation.route())) {
    // throw new Exception("duplicate route by " + WebPath.class.getName());
    // }
    // annotationMap.put(annotation.methed() + "_" + annotation.route(), aa);
    //
    // }
    // }
    // }
    //
    // }
    // }
}
