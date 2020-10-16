/**
 * 
 */
package HttpServer.controllers;

import java.io.IOException;

import HttpServer.MyHTTPServer;
import HttpServer.MyHTTPServerBuilder;

public class _main {
    public static void main(String[] args) throws IOException {

        MyHTTPServer myHTTPServer2 = MyHTTPServerBuilder.newMyHTTPServer()
                .setHost("127.0.0.1")
                .setBacklog(10)
                .setPort(5000).build();

        myHTTPServer2.go();

    }
}
