/**
 * 
 */
package HttpServer.Aops;

import HttpServer.interfaces.CustomedAOP;

public class AOPdo4 implements CustomedAOP {

    @Override
    public void before(Object[] args) {
        // System.out.println(test);
        System.out.println("before4! " + args[0]);
    }

    @Override
    public void after(Object args) {
        System.out.println("after4! " + args);
    }
}