/**
 * 
 */
package HttpServer.controllers;

import HttpServer.Aops.AOPdo4;
import HttpServer.annotations.AOP;
import HttpServer.annotations.Context;

@Context
public class Db1 implements Dbinterface {

    @AOP(message = AOPdo4.class)
    public int a(int a) {
        return a;
    }
}
