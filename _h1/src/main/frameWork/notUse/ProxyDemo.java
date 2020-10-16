/**
 * 
 */
package main.frameWork.notUse;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import main.frameWork.SimpleProxyHandler;
import main.frameWork.Aops.AOPdo1;
import main.frameWork.Aops.AOPdo2;
import main.frameWork.Aops.AOPdo3;
import main.frameWork.annotatoins.AOP;

public class ProxyDemo {
    public static void main(String[] args) throws Exception {
        SimpleProxyHandler logHandler = new SimpleProxyHandler();

        Map<Class<?>, Object> map = new HashMap<>();
        map.put(AOPdo1.class, new AOPdo1());
        // HelloSpeaker hh = new HelloSpeaker();

        /////////////
        HelloSpeaker HH = new HelloSpeaker();
        IHello helloProxy = (IHello) logHandler.bind(HH);
        // helloProxy.hello("Justin");
        // helloProxy.hello2("Harry");
        // helloProxy.hello3("Mike");

        Object obj = helloProxy;
        HH.getClass().getInterfaces()[0].getClass();
        IHello aaa = IHello.class.cast(obj);

        Method mm = aaa.getClass().getMethod("hello3", String.class);
        System.out.println(aaa.getClass().getName());
        // aaa.hello3("Mike");
        mm.invoke(aaa, "123");
        System.out.println(obj.getClass().getTypeName());

        // helloProxy.hello("Harry");
        // helloProxy.nope();
        // helloProxy.yes();
    }

}

@AOP(message = AOPdo3.class)
class HelloSpeaker implements IHello {

    @AOP(message = AOPdo1.class)
    public void hello(String name) {
        System.out.println("Hello, " + name);
    }

    @AOP(message = AOPdo2.class)
    public void hello2(String name) {
        System.out.println("Hello, " + name);
    }

    public void hello3(String name) {
        System.out.println("Hello, " + name);
    }

    public void nope() {
        System.out.println("NO!");
    }

    public void yes() {
        System.out.println("YES!");
    }
}