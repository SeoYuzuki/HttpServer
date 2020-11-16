/**
 * 
 */
package main.frameWork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.frameWork.beans.BeanResource;

public class SeoYuzukiServer {

    private ServerSocket server = null;
    private int soTimeout = 0;
    BeanResource beanResource;

    public SeoYuzukiServer(ServerSocket server, int soTimeout, BeanResource beanResource) {
        this.server = server;
        this.soTimeout = soTimeout;
        this.beanResource = beanResource;
    }

    public void go() throws IOException {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        while (true) {
            Socket connected = server.accept();
            connected.setSoTimeout(soTimeout);

            cachedThreadPool.execute(new MyHTTPServerCore(connected, beanResource));
        }

    }

}