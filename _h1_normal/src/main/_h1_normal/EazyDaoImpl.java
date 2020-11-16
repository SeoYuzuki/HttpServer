/**
 * 
 */
package main._h1_normal;

import main.controller.aops.AOPdo4;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Context;

@Context
public class EazyDaoImpl {

    @AOP(value = AOPdo4.class)
    public int a(int a) {
        return a;
    }
}
