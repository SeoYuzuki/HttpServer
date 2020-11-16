/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;

@AopAdvice
public class AOPtest4 {

    @AopOnBefore
    public void before(Object[] args) {
        System.out.println("before4! " + args + " length:" + args.length);
    }

    @AopOnAfter
    public void after(Object args) {
        System.out.println("after4! ");
    }

}