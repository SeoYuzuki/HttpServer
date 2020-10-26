/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;

@AopAdvice
public class AOPdo1 {

    @AopOnBefore
    public void before(Object[] args) {
        // System.out.println(test);
        System.out.println("before1! ");
    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after1! " + args);
    }

}