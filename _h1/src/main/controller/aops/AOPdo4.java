/**
 * 
 */
package main.controller.aops;

import main.frameWork.interfaces.CustomedAOP;

public class AOPdo4 implements CustomedAOP {

    @Override
    public void before(Object[] args) {        
        System.out.println("before4! " + args[0]);
    }

    @Override
    public void after(Object args) {
        System.out.println("after4! " + args);
    }

    @Override
    public String getJsEmbeddedPath() {        
        return null;
    }
}