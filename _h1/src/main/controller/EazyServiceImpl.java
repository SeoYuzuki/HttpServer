/**
 * 
 */
package main.controller;

import java.util.concurrent.CompletableFuture;

import main.controller.aops.AOPtest3;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Async;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;

@Context
public class EazyServiceImpl implements EazyService {
    @Autowired
    public EazyDaoImpl dd;

    @AOP(value = AOPtest3.class)
    public int getXandY(int a, int b) {

        return dd.a(a + b);
    }

    public int getXsubY(int i1, int ib) {

        return i1 - ib;
    }

    @Async
    public void asyncTest() {

        try {
            Thread.sleep(5000);
            System.out.println("~yo 5s");
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }

    @Async
    public CompletableFuture<String> asyncTest2() {

        try {
            Thread.sleep(5000);
            System.out.println("~yo 5s");
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("asyncTest2 completed");

    }
}
