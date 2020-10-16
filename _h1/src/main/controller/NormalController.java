/**
 * 
 */
package main.controller;

import java.io.IOException;

import main.frameWork.beans.HttpRequest;
import main.frameWork.beans.HttpResponse;

public interface NormalController {

    public void doGet(HttpRequest req, HttpResponse resp) throws IOException;

    public void doVi(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet2(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet3(HttpRequest req, HttpResponse resp) throws IOException;

    public void doGet4(HttpRequest req, HttpResponse resp);

    public void doPOST4(HttpRequest req, HttpResponse resp) throws IOException;

    public void responsefile(HttpRequest req, HttpResponse resp) throws IOException;

}
