/**
 * 
 */
package main.controller.aops;

import main.frameWork.Resources;
import main.frameWork.interfaces.CustomedAOP;

public class AOPdo2 implements CustomedAOP {
    @Override
    public String getJsEmbeddedPath() {
        return Resources.whereMainAtNoBin + "controller\\embedded\\AOPdo2.js";
    }

    @Override
    public void before(Object[] args) {

        System.out.println("before2! ");
        // System.out.println("before2! " + args[0]);

    }

    @Override
    public void after(Object args) {
        System.out.println("after2! " + args);
    }

}
