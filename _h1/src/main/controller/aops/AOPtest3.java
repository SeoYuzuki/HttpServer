/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;

@AopAdvice
public class AOPtest3 {

    @AopOnBefore
    public void before(Object[] args) {
        System.out.println("before3! ");
    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after3! " + args);
    }

}