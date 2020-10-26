/**
 * 
 */
package main.frameWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;

import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;
import main.frameWork.annotatoins.JsEmbeddedPath;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxyHandler implements MethodInterceptor {

    @Override
    public Object intercept(Object delegate, Method invokeMethod, Object[] args, MethodProxy proxy) throws Throwable {
        Object returnObject = null;

        try {
            Object aopObj = extractAOPclass(delegate, invokeMethod);
            // before
            // System.out.println("++++++" + invokeMethod.getName());
            if (aopObj != null) {
                before(aopObj, args);
            }
            // invoke
            returnObject = proxy.invokeSuper(delegate, args);

            // after
            if (aopObj != null) {
                Method mAfter = null;
                for (Method mm : aopObj.getClass().getMethods()) {
                    if (mm.getAnnotation(AopOnAfter.class) != null) {
                        mAfter = mm;
                        break;
                    }
                }
                // Method mAfter = aopObj.getClass().getMethod("after", Object.class);
                mAfter.invoke(aopObj, returnObject);
            }

            // System.out.println("After invoked method name: " + invokeMethod.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnObject;
    }

    private void before(Object aopObj, Object[] args) {
        try {

            jSembedded(aopObj, args);

            Method mBefore = null;
            for (Method mm : aopObj.getClass().getMethods()) {
                if (mm.getAnnotation(AopOnBefore.class) != null) {
                    mBefore = mm;
                    break;
                }
            }

            // Method mBefore = aopObj.getClass().getMethod("before", Object[].class);

            if (mBefore != null && args.length > 0) {
                mBefore.invoke(aopObj, new Object[] {args });// 必需用 new Object[]{s} 這種特殊的型式來告知compiler 我想傳的是 s 而非 s[0],s[1]

            } else {
                mBefore.invoke(aopObj, new Object[] {args });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 從真實的原物件取得annotation 還有annotation所記載的AOPclass
    private Object extractAOPclass(Object delegate, Method invokeMethod) {
        try {
            Map<Class<?>, Object> map = Resources.aopsMap;// new HashMap<>();
            Class<?>[] delegateMethodParas = new Class[invokeMethod.getParameters().length];
            for (int i = 0; i < invokeMethod.getParameters().length; i++) {
                delegateMethodParas[i] = invokeMethod.getParameters()[i].getType();
            }

            String realClassName = delegate.getClass().getName().split("\\$\\$")[0];
            Object realObj = Resources.beanMap.get(Class.forName(realClassName)).getRealObject();
            realObj.getClass().getMethod(invokeMethod.getName(), delegateMethodParas);
            Method realMethod = realObj.getClass().getMethod(invokeMethod.getName(), delegateMethodParas);

            Class<?> aopClass;
            if (realMethod.getAnnotation(AOP.class) != null) { // method有AOP
                aopClass = realMethod.getAnnotation(AOP.class).value();
            } else if (realObj.getClass().getAnnotation(AOP.class) != null) {// method沒有AOP, 但是class有
                aopClass = realObj.getClass().getAnnotation(AOP.class).value();
            } else {
                aopClass = null;
            }
            // System.out.println("aopClass:" + aopClass);
            Object aopObj = map.get(aopClass);

            return aopObj;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void jSembedded(Object aopObj, Object[] o) {
        try {
            int i = 0;

            String JsEmbeddedPath = "";
            for (Field ff : aopObj.getClass().getFields()) {
                if (ff.getAnnotation(JsEmbeddedPath.class) != null) {
                    JsEmbeddedPath = (String) ff.get(aopObj);
                    break;
                }
            }

            if (JsEmbeddedPath.length() < 1) {
                return;
            }

            // System.out.println("-----" + aopObj.getClass() + " " + JsEmbeddedPath);
            if (JsEmbeddedPath != null && !JsEmbeddedPath.equals("")) {
                File f = new File(JsEmbeddedPath);
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
}