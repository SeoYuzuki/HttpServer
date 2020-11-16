/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;
import main.frameWork.annotatoins.JsEmbeddedPath;

@AopAdvice
public class AOPtest2 {
    @JsEmbeddedPath
    String s = "embedded\\AOPdo2.js";

    @AopOnBefore
    public void before(Object[] args) {

        System.out.println("before2! ");
        // System.out.println("before2! " + args[0]);

    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after2! " + args);
    }

}
