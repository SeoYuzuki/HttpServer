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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;
import main.frameWork.annotatoins.AopOnError;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;
import main.frameWork.annotatoins.JsEmbeddedPath;
import main.frameWork.annotatoins.WebPath;
import main.frameWork.annotatoins.WsOnClose;
import main.frameWork.annotatoins.WsOnMessage;
import main.frameWork.annotatoins.WsOnOpen;
import main.frameWork.annotatoins.WsServerEndpoint;
import main.frameWork.beans.AdviceBean;
import main.frameWork.beans.BeanResource;
import main.frameWork.beans.MethodsWithObjs;
import main.frameWork.beans.ObjWithProxy;
import net.sf.cglib.proxy.Enhancer;

public class AnnotationsSetUp {
    private ArrayList<MethodsWithObjs> arr;
    private Map<String, MethodsWithObjs> annotationMap;
    private Map<Class<?>, ObjWithProxy> beanMap;
    private Map<Class<?>, AdviceBean> advicesMap;
    private BeanResource beanResource;

    /**
     * 依照 annotation Controller.class 和 WebPath.class 篩選method存入map
     * @param beanResource
     * @param annotationMap
     * @throws Exception
     * @remark
     */
    static public void ScanAnnotations(BeanResource beanResource) {
        try {

            AnnotationsSetUp refl = new AnnotationsSetUp(beanResource);
            refl.ScanClassesWithAnnotation();
            refl.doAutoWired();
            refl.doController();
            refl.doWS();

            System.out.println("bean:" + refl.beanMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AnnotationsSetUp(BeanResource beanResource) {
        this.beanResource = beanResource;
        annotationMap = beanResource.getAnnotationMap();
        beanMap = beanResource.getBeanMap();
        advicesMap = beanResource.getAdvicesMap();

        arr = new ArrayList<>();
    }

    /**
     * 掃描有壓Annotation的class, 並且做對應動作
     * 
     * @remark
     */
    private void ScanClassesWithAnnotation() {
        try {

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

                                if (isAnnotaionExtend(loopClass, AopAdvice.class)) {
                                    AdviceBean aopsMapBean = new AdviceBean();
                                    Object aopObj = loopClass.newInstance();
                                    aopsMapBean.setAopObj(aopObj);
                                    for (Field ff : aopObj.getClass().getDeclaredFields()) {
                                        if (ff.getAnnotation(JsEmbeddedPath.class) != null) {
                                            ff.setAccessible(true);
                                            aopsMapBean.setJsEmbeddedPath((String) ff.get(aopObj));
                                            break;
                                        }
                                    }

                                    for (Method mm : aopObj.getClass().getDeclaredMethods()) {
                                        if (mm.getAnnotation(AopOnAfter.class) != null) {
                                            mm.setAccessible(true);
                                            aopsMapBean.setAopOnAfterMethod(mm);
                                            AopOnAfter AopOnAfter = mm.getAnnotation(AopOnAfter.class);
                                            if (AopOnAfter.doAfterError()) {
                                                aopsMapBean.doAfterError(true);
                                            }
                                            break;
                                        }
                                    }

                                    for (Method mm : aopObj.getClass().getDeclaredMethods()) {
                                        if (mm.getAnnotation(AopOnBefore.class) != null) {
                                            mm.setAccessible(true);
                                            aopsMapBean.setAopOnBeforeMethod(mm);
                                            break;
                                        }
                                    }

                                    for (Method mm : aopObj.getClass().getDeclaredMethods()) {
                                        if (mm.getAnnotation(AopOnError.class) != null) {
                                            mm.setAccessible(true);
                                            aopsMapBean.setAopOnErrorMethod(mm);
                                            break;
                                        }
                                    }

                                    advicesMap.put(loopClass, aopsMapBean);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMethodsWithObjsToList(Class<?> loopClass) {
        try {
            Constructor<?> constructor = loopClass.getDeclaredConstructors()[0];
            Object realObj = constructor.newInstance();

            ObjWithProxy op = makeObjWithProxy(realObj);
            Object proxy = op.getProxy();

            // System.out.println("loopClass:" + loopClass.getName());

            beanMap.put(loopClass, op); // 先放入impl
            if (loopClass.getInterfaces().length > 0) {// 如果有介面
                beanMap.put(loopClass.getInterfaces()[0], op);
            }

            for (int i = 0; i < loopClass.getDeclaredMethods().length; i++) {
                MethodsWithObjs mObj = new MethodsWithObjs();
                mObj.setRealObj(realObj);

                // method層級
                Method mm = loopClass.getDeclaredMethods()[i];
                mm.setAccessible(true);
                mObj.setRealMethod(mm);
                // if method或class有壓AOP
                // if (mm.getAnnotation(Async.class) != null
                // || mm.getAnnotation(AOP.class) != null
                // || loopClass.getAnnotation(AOP.class) != null) {
                mObj.setProxyed(true);
                mObj.setProxyObj(proxy);

                for (Method proxyM : proxy.getClass().getDeclaredMethods()) {
                    if (proxyM.getName().equals(mm.getName())) {
                        proxyM.setAccessible(true);
                        mObj.setProxyMethod(proxyM);
                        break;
                    }
                }
                if (mObj.getProxyMethod() == null) {
                    // throw new Exception("ProxyMethod不該為null");
                }
                // }

                arr.add(mObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 製作proxy物件
    private ObjWithProxy makeObjWithProxy(Object realObj) {
        CglibProxyHandler someProxy = new CglibProxyHandler(beanResource);

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(realObj.getClass());
        enhancer.setCallback(someProxy);

        Object proxy = enhancer.create();// 製作proxy物件
        return new ObjWithProxy(realObj, proxy);
    }

    private void doAutoWired() throws Exception {
        for (Entry<Class<?>, ObjWithProxy> en : beanMap.entrySet()) {
            // System.out.println("!!" + en.getValue().realObject.getClass());
            for (Field fi : en.getValue().getRealObject().getClass().getDeclaredFields()) {

                if (fi.getAnnotation(Autowired.class) != null) {
                    fi.setAccessible(true);
                    // System.out.println(fi.getName() + " / " + fi.getType());
                    if (beanMap.containsKey(fi.getType())) {
                        fi.set(en.getValue().getRealObject(), beanMap.get(fi.getType()).getProxy());
                        fi.set(en.getValue().getProxy(), beanMap.get(fi.getType()).getProxy());

                    }

                }
            }

        }
    }

    private void doController() throws MyHTTPException {
        for (MethodsWithObjs mm : arr) {
            WebPath anno = mm.getRealMethod().getAnnotation(WebPath.class);
            if (anno != null) {
                if (annotationMap.containsKey(anno.methed() + "_" + anno.route())) {
                    System.out.println("??" + anno.methed() + "_" + anno.route());
                    throw new MyHTTPException("duplicate route by WebPath");
                }
                annotationMap.put(anno.methed() + "_" + anno.route(), mm);
            }
        }
    }

    private void doWS() throws MyHTTPException {
        for (MethodsWithObjs mm : arr) {
            WsServerEndpoint wsServerEndpoint = mm.getRealObj().getClass().getAnnotation(WsServerEndpoint.class);

            if (wsServerEndpoint != null) {
                String classRoute = wsServerEndpoint.route();
                WsOnOpen wsOnOpen = mm.getRealMethod().getAnnotation(WsOnOpen.class);
                if (wsOnOpen != null) {
                    if (annotationMap.containsKey(classRoute + "#WsOnOpen")) {
                        throw new MyHTTPException("duplicate route by WsOnOpen");
                    }
                    annotationMap.put(classRoute + "#WsOnOpen", mm);
                    continue;
                }
                WsOnMessage wsOnMessage = mm.getRealMethod().getAnnotation(WsOnMessage.class);
                if (wsOnMessage != null) {
                    if (annotationMap.containsKey(classRoute + "#WsOnMessage_" + wsOnMessage.TypeOfFrame())) {
                        throw new MyHTTPException("duplicate route by wsOnMessage");
                    }
                    annotationMap.put(classRoute + "#WsOnMessage_" + wsOnMessage.TypeOfFrame(), mm);
                    continue;
                }
                WsOnClose wsOnClose = mm.getRealMethod().getAnnotation(WsOnClose.class);
                if (wsOnClose != null) {
                    if (annotationMap.containsKey(classRoute + "#WsOnClose")) {
                        throw new MyHTTPException("duplicate route by WsOnClose");
                    }
                    annotationMap.put(classRoute + "#WsOnClose", mm);
                    continue;
                }
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
