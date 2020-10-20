/**
 * 
 */
package main.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import main.controller.aops.AOPdo1;
import main.controller.aops.AOPdo2;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Context;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.WebPath;
import main.frameWork.beans.HttpRequest;
import main.frameWork.beans.HttpResponse;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl implements NormalController {
    @Autowired
    public EazyService service1;

    @WebPath(methed = "GET", route = "/")
    public void doGet(HttpRequest req, HttpResponse resp) throws IOException {
        try {
            String realPath = "D:\\learning\\main.html";
            // System.out.println("realPath:" + realPath);
            byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());
            resp.setResponseData(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @WebPath(methed = "GET", route = "/s1/vi")
    public void doVi(HttpRequest req, HttpResponse resp) throws IOException {
        String realPath = "s1\\vi.html";

        try {
            resp.renderHtml(realPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @WebPath(methed = "GET", route = "/s1/jp")
    @AOP(AOPdo1.class)
    public void doGet2(HttpRequest req, HttpResponse resp) throws IOException {
        try {
            String path = "s1/jp.html";

            resp.renderHtml(path);

            // String realPath = "D:\\learning\\s1/jp.html";
            // // System.out.println("realPath:" + realPath);
            // byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());
            // resp.setResponseData(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "GET", route = "/s1/sum2")
    @AOP(AOPdo2.class)
    public void doGet3(HttpRequest req, HttpResponse resp) throws IOException {

        Map<String, String> map = req.getURLParameterMap();
        System.out.println("MAP:" + map);
        try {
            String path = "s1\\sum2.html";
            System.out.println("realPath:" + path);
            if (req.getURLParameterMap() != null) {
                int i1 = Integer.parseInt((String) req.getURLParameterMap().get("numa"));
                int ib = Integer.parseInt((String) req.getURLParameterMap().get("numb"));
                int sum = service1.getXandY(i1, ib);

                int sub = service1.getXsybY(i1, ib);

                resp.renderHtml(path)
                        .addData("SUM", "相加:" + sum)
                        .addData("SUB", "相減:" + sub);

            } else {
                resp.renderHtml(path)
                        .addData("SUM", "")
                        .addData("SUB", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "GET", route = "/s1/coo")
    public void doGet4(HttpRequest req, HttpResponse resp) {

        try {
            String realPath = "s1\\coo.html";
            System.out.println("realPath:" + realPath);
            resp.renderHtml(realPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "POST", route = "/s1/coo")
    public void doPOST4(HttpRequest req, HttpResponse resp) throws IOException {
        Map<String, String> map = req.getURLParameterMap();
        System.out.println("MAP:" + map);
        try {
            String path = "s1\\coo.html";
            System.out.println("realPath:" + path);
            resp.cookie("yourName", req.getPostBodyMap().get("name") + "!!!");
            resp.cookie("yourPasswd", req.getPostBodyMap().get("passwd") + "!!!");
            resp.renderHtml(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "GET", route = "file")
    public void responsefile(HttpRequest req, HttpResponse resp) throws IOException {

        try {
            // System.out.println("realPath:" + mainPath + req.getRequestURI());

            // resp.renderFile(mainPath + req.getRequestURI());
            resp.renderFile(req.getRequestURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
