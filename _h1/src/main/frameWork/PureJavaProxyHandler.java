/**
 * 
 */
package main.frameWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;

import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.beans.AopsMapBean;

public class PureJavaProxyHandler implements InvocationHandler {
    @Autowired
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Object delegate;

    public Object bind(Object delegate) {
        this.delegate = delegate;

        return Proxy.newProxyInstance(
                delegate.getClass().getClassLoader(),
                delegate.getClass().getInterfaces(),
                this);
    }

    @Override
    public Object invoke(Object proxy, Method invokeMethod, Object[] args) throws Throwable {
        Object result = null;

        try {
            log("method starts..." + invokeMethod);
            System.out.println("invokeMethod.getName():" + invokeMethod.getName());
            Map<Class<?>, AopsMapBean> map = Resources.aopsMap;// new HashMap<>();
            // map.put(AOPdo1.class, new AOPdo1());
            // map.put(AOPdo2.class, new AOPdo2());
            // map.put(AOPdo3.class, new AOPdo3());

            Class<?>[] delegateMethodParas = new Class[invokeMethod.getParameters().length];
            for (int i = 0; i < invokeMethod.getParameters().length; i++) {
                delegateMethodParas[i] = invokeMethod.getParameters()[i].getType();
            }
            Method delegateMethod = delegate.getClass().getMethod(invokeMethod.getName(), delegateMethodParas);

            Class<?> aopClass;
            if (delegateMethod.getAnnotation(AOP.class) != null) { // method有AOP
                aopClass = delegateMethod.getAnnotation(AOP.class).value();
            } else if (delegate.getClass().getAnnotation(AOP.class) != null) {// method沒有AOP, 但是class有
                aopClass = delegate.getClass().getAnnotation(AOP.class).value();
            } else {
                aopClass = null;
            }

            Object aopObj = map.get(aopClass);
            // before
            before(aopClass, aopObj, args);

            // invoke
            result = invokeMethod.invoke(delegate, args);

            // after
            if (aopClass != null) {
                Method mAfter = map.get(aopClass).getClass().getMethod("after", Object.class);
                mAfter.invoke(map.get(aopClass), result);
            }

            log("method ends..." + invokeMethod);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private void log(String message) {
        // logger.log(Level.INFO, message);
    }

    public void before(Class<?> aopClass, Object aopObj, Object[] args) {
        try {
            if (aopClass != null) {

                jSembedded(aopObj, args);

                Method mBefore = aopObj.getClass().getMethod("before", Object[].class);
                // 必需用 new Object[]{s} 這種特殊的型式來告知compiler 我想傳的是 s 而非 s[0],s[1]
                mBefore.invoke(aopObj, new Object[] {args });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void jSembedded(Object aopObj, Object[] o) throws Exception {
        int i = 0;
        Method getjspathM = aopObj.getClass().getMethod("getJsEmbeddedPath");
        String jsPath = (String) getjspathM.invoke(aopObj);

        if (jsPath != null && !jsPath.equals("")) {
            File f = new File(jsPath);
            if (f.exists() && !f.isDirectory()) {

                Gson gson = new Gson();
                String inJson = gson.toJson(o[i]);
                // System.out.println("-----!!");
                // System.out.println(inJson);
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
    }
}