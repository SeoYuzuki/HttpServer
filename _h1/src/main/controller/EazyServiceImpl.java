/**
 * 
 */
package main.controller;

import main.controller.aops.AOPdo3;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;

@Context
public class EazyServiceImpl implements EazyService {
    @Autowired
    public EazyDaoImpl dd;

    @AOP(value = AOPdo3.class)
    public int getXandY(int a, int b) {

        return dd.a(a + b);
    }

    public int getXsubY(int i1, int ib) {

        return i1 - ib;
    }

}
