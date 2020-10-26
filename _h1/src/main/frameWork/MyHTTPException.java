/**
 * 
 */
package main.frameWork;

public class MyHTTPException extends Exception {

    private static final long serialVersionUID = -5402810373397582007L;

    boolean isWebsocket = false;

    public MyHTTPException() {
        super();
    }

    public MyHTTPException(String msg) {
        super(msg);
    }

    public MyHTTPException isWebsocket(boolean b) {
        isWebsocket = b;
        return this;
    }
}