/**
 * 
 */
package main.controller.aops;

import main.frameWork.annotatoins.AopAdvice;
import main.frameWork.annotatoins.AopOnAfter;
import main.frameWork.annotatoins.AopOnBefore;
import main.frameWork.annotatoins.AopOnError;

@AopAdvice
public class AOPdo1 {

    @AopOnBefore
    public void before(Object[] args) {
        // System.out.println(test);
        System.out.println("before1! ");
    }

    @AopOnAfter(doAfterError = true)
    public void after(Object args) {
        System.out.println("after1! " + args);
    }

    @AopOnError
    public void error(Throwable e) {
        System.out.println("error! " + e.toString());
    }

}