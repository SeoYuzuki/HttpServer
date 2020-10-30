/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;

@AopAdvice
public class AOPdo5 {

    @AopOnBefore
    public void before(Object[] args) {
        System.out.println("before5! " + args + " length:" + args.length);
    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after5! ");
    }

}