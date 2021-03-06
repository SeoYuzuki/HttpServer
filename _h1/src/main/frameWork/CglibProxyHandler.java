/**
 * 
 */
package main.frameWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;

import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Async;
import main.frameWork.beans.AdviceBean;
import main.frameWork.beans.BeanResource;
import net.sf.cglib.proxy.InvocationHandler;

public class CglibProxyHandler implements InvocationHandler {

    BeanResource beanResource;

    public CglibProxyHandler(BeanResource beanResource) {
        this.beanResource = beanResource;
    }

    @Override
    public Object invoke(Object proxy, Method invokeMethod, Object[] args) throws Throwable {
        Object lastCurrentProxy = Resources.currentProxy.get();
        Resources.currentProxy.set(proxy);

        try {
            Object returnObject = null;
            Object realObj = getRealObject(proxy);
            Method realMethod = getRealMethod(realObj, invokeMethod);
            try {
                AdviceBean aopsMapBean = extractAdviceClass(realObj, realMethod);
                // before
                // System.out.println("++++++" + invokeMethod.getName());
                if (aopsMapBean != null) {
                    before(aopsMapBean, args);
                }

                // invoke
                try {
                    if (isAsync(proxy, invokeMethod)) {
                        if (invokeMethod.getReturnType() == void.class) {
                            doAsyncNoReturn(realObj, args, invokeMethod);

                        } else {
                            // 優先回傳一個假future, 給予caller method
                            returnObject = doAsyncWithReturn(realObj, args, invokeMethod);
                        }

                    } else {
                        returnObject = realMethod.invoke(realObj, args);
                    }

                } catch (Throwable e) {
                    if (aopsMapBean != null) {
                        error(aopsMapBean, e);
                        if (!aopsMapBean.isDoAfterError()) {// 錯誤時不做after
                            return returnObject;
                        }
                    } else {
                        throw e;
                    }
                    // e.printStackTrace();
                }

                // after
                if (aopsMapBean != null) {
                    Method mAfter = aopsMapBean.getAopOnAfterMethod();
                    mAfter.invoke(aopsMapBean.getAopObj(), returnObject);
                }

                // System.out.println("After invoked method name: " + invokeMethod.getName());
            } catch (Throwable e) {
                throw e;

            }

            return returnObject;
        } finally {
            Resources.currentProxy.set(lastCurrentProxy);
        }
    }

    private void doAsyncNoReturn(Object delegate, Object[] args, Method proxy) {
        new Thread() {
            public void run() {
                try {
                    proxy.invoke(delegate, args);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 這裡會開新thread
     */
    private CompletableFuture doAsyncWithReturn(Object delegate, Object[] args, Method method) {
        // 優先建立並回傳一個假future, 給予caller method
        CompletableFuture fakeCompletableFuture = new CompletableFuture<>();

        // 開新線程等待結果, 結果出來後再賦予給假future
        try {
            new Thread(() -> {
                try {
                    CompletableFuture<?> ob = (CompletableFuture<?>) method.invoke(delegate, args);
                    // ob.get();
                    fakeCompletableFuture.complete(ob.get());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fakeCompletableFuture;
    }

    private void error(AdviceBean aopsMapBean, Throwable e) throws Throwable {
        Method errorMethod = aopsMapBean.getAopOnErrorMethod();
        if (errorMethod != null) {
            errorMethod.invoke(aopsMapBean.getAopObj(), new Object[] {e });
        } else {
            throw e;
        }
    }

    private void before(AdviceBean aopsMapBean, Object[] args) {
        try {

            jSembedded(aopsMapBean.getJsEmbeddedPath(), args);

            Method mBefore = aopsMapBean.getAopOnBeforeMethod();

            // Method mBefore = aopObj.getClass().getMethod("before", Object[].class);

            if (mBefore != null) {
                if (args.length > 0) {
                    mBefore.invoke(aopsMapBean.getAopObj(), new Object[] {args });// 必需用 new Object[]{s} 這種特殊的型式來告知compiler 我想傳的是 s 而非 s[0],s[1]

                } else {
                    mBefore.invoke(aopsMapBean.getAopObj(), new Object[] {args });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isAsync(Object delegate, Method invokeMethod) {
        try {
            if (invokeMethod.getAnnotation(Async.class) != null) { // method有AOP
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 從真實的原物件取得annotation 還有annotation所記載的Advice class
    private AdviceBean extractAdviceClass(Object realObj, Method realMethod) {
        try {

            Class<?> aopClass;
            if (realMethod.getAnnotation(AOP.class) != null) { // method有AOP
                aopClass = realMethod.getAnnotation(AOP.class).value();
            } else if (realObj.getClass().getAnnotation(AOP.class) != null) {// method沒有AOP, 但是class有
                aopClass = realObj.getClass().getAnnotation(AOP.class).value();
            } else {
                aopClass = null;
            }
            // System.out.println("aopClass:" + aopClass);
            AdviceBean aopsMapBean = beanResource.getAdvicesMap().get(aopClass);

            return aopsMapBean;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void jSembedded(String JsEmbeddedPath, Object[] o) {
        try {
            if (JsEmbeddedPath.length() < 1) {
                return;
            }
            int i = 0; // 目前只嵌入第一個物件 TODO
            // System.out.println("-----JsEmbeddedPath" + " " + JsEmbeddedPath);
            if (JsEmbeddedPath != null && !JsEmbeddedPath.equals("")) {
                File f = new File(beanResource.getWhereMainAtNoBin() + "resource/web/" + JsEmbeddedPath);
                if (f.exists() && !f.isDirectory()) {

                    Gson gson = new Gson();
                    String inJson = gson.toJson(o[i]);
                    // System.out.println("-----!!");
                    // System.out.println("++" + inJson);
                    ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
                    try (Scanner myReader = new Scanner(f)) {
                        String jsFunction = "";
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            jsFunction = jsFunction + data + "\r\n";
                        }
                        // System.out.println(jsFunction);
                        Object result1 = engine.eval(jsFunction);
                        Object result2 = engine.eval("a(" + inJson + ");");

                        Object opJson = gson.fromJson((String) result2, o[i].getClass());

                        // System.out.println("result2:" + result2);

                        o[i] = opJson;

                    } catch (FileNotFoundException | ScriptException e) {

                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Method getRealMethod(Object realObj, Method invokeMethod) throws Exception {
        Class<?>[] delegateMethodParas = new Class[invokeMethod.getParameters().length];
        for (int i = 0; i < invokeMethod.getParameters().length; i++) {
            delegateMethodParas[i] = invokeMethod.getParameters()[i].getType();
        }
        Method mm = realObj.getClass().getDeclaredMethod(invokeMethod.getName(), delegateMethodParas);
        mm.setAccessible(true);
        return mm;

    }

    /**
     * 
     * 必須做字串處理取得原realclass的名, 找到bean中真實的物件, 再去取得該物件所壓的aop annotation
     */
    private Object getRealObject(Object delegate) throws Exception {
        String realClassName = delegate.getClass().getName().split("\\$\\$")[0];
        // Object realObj = Resources.beanMap.get(Class.forName(realClassName)).getRealObject();
        Object realObj = beanResource.getBeanMap().get(Class.forName(realClassName)).getRealObject();

        return realObj;
    }

}