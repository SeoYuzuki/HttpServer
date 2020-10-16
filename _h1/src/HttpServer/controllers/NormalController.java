/**
 * 
 */
package HttpServer.controllers;

import java.io.IOException;

import HttpServer.beans.HttpRequest;
import HttpServer.beans.HttpResponse;

public interface NormalController {

    public void doGet(HttpRequest req, HttpResponse resp) throws IOException;

    public void doVi(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet2(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet3(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet4(HttpRequest req, HttpResponse resp);

    public void doPOST4(HttpRequest req, HttpResponse resp) throws IOException;

    public void responsefile(HttpRequest req, HttpResponse resp) throws IOException;

}
