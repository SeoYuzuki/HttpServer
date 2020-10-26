/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;

@AopAdvice
public class AOPdo3 {

    @AopOnBefore
    public void before(Object[] args) {
        System.out.println("before3! " + args[0]);
    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after3! " + args);
    }

}