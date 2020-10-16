/**
 * 
 */
package main.controller;

import main.frameWork.Aops.AOPdo4;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Context;

@Context
public class EazyDaoImpl implements EazyDao {

    @AOP(message = AOPdo4.class)
    public int a(int a) {
        return a;
    }
}
