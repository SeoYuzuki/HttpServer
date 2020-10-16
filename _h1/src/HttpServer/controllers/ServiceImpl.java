/**
 * 
 */
package HttpServer.controllers;

import HttpServer.Aops.AOPdo2;
import HttpServer.Aops.AOPdo3;
import HttpServer.annotations.AOP;
import HttpServer.annotations.Autowired;
import HttpServer.annotations.Context;

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
