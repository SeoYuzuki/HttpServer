/**
 * 
 */
package main._h1_normal;

import java.io.IOException;

import main.frameWork.MyHTTPException;
import main.frameWork.SeoYuzukiFrameWork;
import main.frameWork.SeoYuzukiServer;
import main.frameWork.SeoYuzukiServerBuilder;

public class _main3 {
    public static void main(String[] args) throws IOException, MyHTTPException {
        String version = System.getProperty("java.version");
        System.out.println("java.version:" + version);

        SeoYuzukiFrameWork seoYuzuki = new SeoYuzukiFrameWork(_main3.class);
        SeoYuzukiServer myHTTPServer2 = SeoYuzukiServerBuilder.newMyHTTPServer()
                .setBeanResource(seoYuzuki.getBeanResource())
                .setHost("127.0.0.1")
                .setBacklog(10)
                .setPort(5001)
                .setSoTimeoutForConnection(1000 * 60 * 5)
                .build();

        myHTTPServer2.go();

    }
}
