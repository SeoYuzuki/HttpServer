/**
 * 
 */
package main.frameWork;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import main.frameWork.beans.BeanResource;

public class SeoYuzukiServerBuilder {
    private int port = 0;
    private String host = "";
    private int soTimeout = 0;
    private int soTimeoutForConnection = 0;
    private int backlog = 0;
    private BeanResource beanResource;

    public static SeoYuzukiServerBuilder newMyHTTPServer() {
        SeoYuzukiServerBuilder builder = new SeoYuzukiServerBuilder();
        return builder;
    }

    public SeoYuzukiServerBuilder setHost(String byName) {
        host = byName;
        return this;
    }

    public SeoYuzukiServerBuilder setPort(int i) {
        port = i;
        return this;
    }

    public SeoYuzukiServerBuilder setSoTimeout(int i) {
        soTimeout = i;
        return this;
    }

    public SeoYuzukiServerBuilder setSoTimeoutForConnection(int i) {
        soTimeoutForConnection = i;
        return this;
    }

    public SeoYuzukiServerBuilder setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public SeoYuzukiServer build() throws MyHTTPException {

        checkBeanResource();

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
        return new SeoYuzukiServer(server, soTimeoutForConnection, beanResource);

    }

    private void checkBeanResource() throws MyHTTPException {
        if (beanResource == null) {
            throw new MyHTTPException("beanResource is null");
        }
    }

    /**
     * @param beanResource
     * @return
     * @remark
     */
    public SeoYuzukiServerBuilder setBeanResource(BeanResource beanResource) {
        this.beanResource = beanResource;
        return this;
    }

}
