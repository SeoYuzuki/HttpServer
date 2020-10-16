/**
 * 
 */
package HttpServer;

public class MyHTTPException extends Exception {

    private static final long serialVersionUID = -5402810373397582007L;

    public MyHTTPException() {
        super();
    }

    public MyHTTPException(String msg) {
        super(msg);
    }
}