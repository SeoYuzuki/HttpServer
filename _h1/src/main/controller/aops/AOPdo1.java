/**
 * 
 */
package main.controller.aops;

import main.frameWork.interfaces.CustomedAOP;

public class AOPdo1 implements CustomedAOP {

    @Override
    public void before(Object[] args) {
        // System.out.println(test);
        System.out.println("before1! ");
    }

    @Override
    public void after(Object args) {
        System.out.println("after1! " + args);
    }

    @Override
    public String getJsEmbeddedPath() {

        return null;
    }
}