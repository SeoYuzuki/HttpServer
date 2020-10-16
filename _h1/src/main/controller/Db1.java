/**
 * 
 */
package main.controller;

import main.frameWork.Aops.AOPdo4;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Context;

@Context
public class Db1 implements Dbinterface {

    @AOP(message = AOPdo4.class)
    public int a(int a) {
        return a;
    }
}
