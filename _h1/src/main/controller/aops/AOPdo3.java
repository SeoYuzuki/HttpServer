/**
 * 
 */
package main.controller.aops;

import main.frameWork.interfaces.CustomedAOP;

public class AOPdo3 implements CustomedAOP {

    @Override
    public void before(Object[] args) {
        System.out.println("before3! " + args[0]);
    }

    @Override
    public void after(Object args) {
        System.out.println("after3! " + args);
    }

    @Override
    public String getJsEmbeddedPath() {
        return null;
    }
}