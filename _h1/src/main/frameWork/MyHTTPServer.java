/**
 * 
 */
package main.frameWork;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyHTTPServer {

    private ServerSocket server = null;
    private int soTimeout = 0;

    public MyHTTPServer(ServerSocket server, int soTimeout) {
        this.server = server;
        this.soTimeout = soTimeout;
    }

    public void go() throws IOException {
        ReflectionsUtil.ScanAnnotations();

        // System.out.println("---" + this.getClass().getResource("../").getPath().substring(1));
        Resources.whereMainAt = this.getClass().getResource("../").getPath().substring(1);
        Resources.whereMainAtNoBin = this.getClass().getResource("../").getPath().substring(1).replace("/bin/", "/src/");
        // System.out.println(Resources.whereMainAtNoBin);
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        // System.out.println(Resources.beanMap);
        while (true) {
            Socket connected = server.accept();
            connected.setSoTimeout(soTimeout);

            cachedThreadPool.execute(new MyHTTPServerCore(connected));
        }

    }

}