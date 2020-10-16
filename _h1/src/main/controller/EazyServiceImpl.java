/**
 * 
 */
package main.controller;

import main.frameWork.Aops.AOPdo3;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;

@Context
public class ServiceImpl implements Service {
    @Autowired
    public Dbinterface dd;

    @AOP(message = AOPdo3.class)
    public int getXandY(int a, int b) {
        return dd.a(a + b);
    }

    public int getXsybY(int i1, int ib) {

        return i1 - ib;
    }

}
