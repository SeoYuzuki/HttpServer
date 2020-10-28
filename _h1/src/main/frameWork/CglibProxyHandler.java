/**
 * 
 */
package main.frameWork;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Scanner;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.google.gson.Gson;

import main.frameWork.annotatoins.AOP;
import main.frameWork.beans.AopsMapBean;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CglibProxyHandler implements MethodInterceptor {

    @Override
    public Object intercept(Object delegate, Method invokeMethod, Object[] args, MethodProxy proxy) throws Throwable {
        Object returnObject = null;

        try {
            AopsMapBean aopsMapBean = extractAOPclass(delegate, invokeMethod);
            // before
            // System.out.println("++++++" + invokeMethod.getName());
            if (aopsMapBean != null) {
                before(aopsMapBean, args);
            }

            // invoke
            try {
                returnObject = proxy.invokeSuper(delegate, args);
            } catch (Throwable e) {

                error(aopsMapBean, e);

                if (!aopsMapBean.isDoAfterError()) {// 錯誤時不做after
                    return returnObject;
                }
            }

            // after
            if (aopsMapBean != null) {
                Method mAfter = aopsMapBean.getAopOnAfterMethod();
                mAfter.invoke(aopsMapBean.getAopObj(), returnObject);
            }

            // System.out.println("After invoked method name: " + invokeMethod.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnObject;
    }

    private void error(AopsMapBean aopsMapBean, Throwable e) throws Exception {
        try {
            Method errorMethod = aopsMapBean.getAopOnErrorMethod();
            if (errorMethod != null) {
                errorMethod.invoke(aopsMapBean.getAopObj(), new Object[] {e });
            } else {
                e.printStackTrace();
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void before(AopsMapBean aopsMapBean, Object[] args) {
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

    // 從真實的原物件取得annotation 還有annotation所記載的AOPclass
    private AopsMapBean extractAOPclass(Object delegate, Method invokeMethod) {
        try {
            Map<Class<?>, AopsMapBean> map = Resources.aopsMap;// new HashMap<>();
            Class<?>[] delegateMethodParas = new Class[invokeMethod.getParameters().length];
            for (int i = 0; i < invokeMethod.getParameters().length; i++) {
                delegateMethodParas[i] = invokeMethod.getParameters()[i].getType();
            }

            // 必須做字串處理取得原realclass的名, 找到bean中真實的物件, 再去取得該物件所壓的aop annotation
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
            AopsMapBean aopsMapBean = map.get(aopClass);

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