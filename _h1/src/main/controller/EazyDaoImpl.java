/**
 * 
 */
package main.controller;

import main.controller.aops.AOPtest4;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Context;

@Context
public class EazyDaoImpl {

    @AOP(value = AOPtest4.class)
    public int a(int a) {
        return a;
    }
}
