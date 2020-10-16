/**
 * 
 */
package HttpServer.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import HttpServer.annotations.AOP;
import HttpServer.annotations.Autowired;
import HttpServer.annotations.Controller;
import HttpServer.annotations.WebPath;
import HttpServer.beans.HttpRequest;
import HttpServer.beans.HttpResponse;
import HttpServer.Aops.AOPdo1;
import HttpServer.Aops.AOPdo2;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl implements NormalController {
    @Autowired
    public Service service1;

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
        String realPath = "D:\\learning\\s1\\vi.html";

        try {
            resp.renderHtml(realPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @WebPath(methed = "GET", route = "/s1/jp")
    @AOP(message = AOPdo1.class)
    public void doGet2(HttpRequest req, HttpResponse resp) throws IOException {
        try {
            String realPath = "D:\\learning\\s1/jp.html";
            System.out.println("realPath:" + realPath);
            byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());
            resp.setResponseData(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "GET", route = "/s1/sum2")
    // @AOP(message = AOPdo2.class)
    public void doGet3(HttpRequest req, HttpResponse resp) throws IOException {
        // Gson a = new Gson();
        // a.fromJson("", classOfT);
        Map<String, String> map = req.getURLParameterMap();
        System.out.println("MAP:" + map);
        try {
            String realPath = "D:\\learning\\s1\\sum2.html";
            System.out.println("realPath:" + realPath);
            if (req.getURLParameterMap() != null) {
                int i1 = Integer.parseInt((String) req.getURLParameterMap().get("numa"));
                int ib = Integer.parseInt((String) req.getURLParameterMap().get("numb"));
                int sum = service1.getXandY(i1, ib);

                int sub = service1.getXsybY(i1, ib);

                resp.renderHtml(realPath)
                        .addData("SUM", "相加:" + sum)
                        .addData("SUB", "相減:" + sub);

            } else {
                resp.renderHtml(realPath)
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
            String realPath = "D:\\learning\\s1\\coo.html";
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
            String realPath = "D:\\learning\\s1\\coo.html";
            System.out.println("realPath:" + realPath);
            resp.cookie("yourName", req.getPostBodyMap().get("name") + "!!!");
            resp.cookie("yourPasswd", req.getPostBodyMap().get("passwd") + "!!!");
            resp.renderHtml(realPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebPath(methed = "GET", route = "file")
    public void responsefile(HttpRequest req, HttpResponse resp) throws IOException {
        String mainPath = "D:\\learning\\";

        try {
            // System.out.println("realPath:" + mainPath + req.getRequestURI());

            resp.renderFile(mainPath + req.getRequestURI());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
