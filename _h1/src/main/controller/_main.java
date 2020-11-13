/**
 * 
 */
package main.controller;

import java.io.IOException;

import main.frameWork.MyHTTPException;
import main.frameWork.SeoYuzukiServer;
import main.frameWork.SeoYuzukiServerBuilder;
import main.frameWork.SeoYuzukiFrameWork;

public class _main {
    public static void main(String[] args) throws IOException, MyHTTPException {
        String version = System.getProperty("java.version");
        System.out.println("java.version:" + version);

        SeoYuzukiFrameWork seoYuzuki = new SeoYuzukiFrameWork(_main.class);

        SeoYuzukiServer myHTTPServer2 = SeoYuzukiServerBuilder.newMyHTTPServer()
                .setBeanResource(seoYuzuki.getBeanResource())
                .setHost("127.0.0.1")
                .setBacklog(10)
                .setPort(5000)
                .setSoTimeoutForConnection(1000 * 60 * 5)
                .build();

        myHTTPServer2.go();

    }
}
