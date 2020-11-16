/**
 * 
 */
package main._h1_normal.cusAOPs;

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
    void after(Object args) {
        @SuppressWarnings("unused")
        String ss = "";

        System.out.println("after1! " + args);
    }

    @AopOnError
    private void error(@AopOnError Throwable e) {
        // System.out.println("error! " + e.toString());
        e.printStackTrace();
    }

}