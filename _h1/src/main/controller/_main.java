/**
 * 
 */
package main.controller;

import java.io.IOException;

import main.frameWork.MyHTTPServer;
import main.frameWork.MyHTTPServerBuilder;
import net.sf.cglib.proxy.Enhancer;

public class _main {
    public static void main(String[] args) throws IOException {
        String version = System.getProperty("java.version");
        System.out.println("java.version:" + version);
        MyHTTPServer myHTTPServer2 = MyHTTPServerBuilder.newMyHTTPServer()
                .setHost("127.0.0.1")
                .setBacklog(10)
                .setPort(5000)
                .setSoTimeoutForConnection(1000 * 60 * 5)
                .build();

        myHTTPServer2.go();

    }
}
