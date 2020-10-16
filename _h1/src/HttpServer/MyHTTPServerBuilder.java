/**
 * 
 */
package HttpServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

public class MyHTTPServerBuilder {
    private int port = 0;
    private String host = "";
    private int soTimeout = 0;
    private int soTimeoutForConnection = 0;
    private int backlog = 0;

    public static MyHTTPServerBuilder newMyHTTPServer() {
        MyHTTPServerBuilder builder = new MyHTTPServerBuilder();
        return builder;
    }

    public MyHTTPServerBuilder setHost(String byName) {
        host = byName;
        return this;
    }

    public MyHTTPServerBuilder setPort(int i) {
        port = i;
        return this;
    }

    public MyHTTPServerBuilder setSoTimeout(int i) {
        soTimeout = i;
        return this;
    }

    public MyHTTPServerBuilder setSoTimeoutForConnection(int i) {
        soTimeoutForConnection = i;
        return this;
    }

    public MyHTTPServerBuilder setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public MyHTTPServer build() {

        ServerSocket server = null;

        try {
            server = new ServerSocket(port, backlog, InetAddress.getByName(host));

            server.setSoTimeout(soTimeout);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("TCPServer Waiting for client on port " + port);
        return new MyHTTPServer(server, soTimeoutForConnection);

    }

}
