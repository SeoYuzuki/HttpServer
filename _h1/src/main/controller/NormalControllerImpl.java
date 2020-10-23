/**
 * 
 */
package main.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import com.google.gson.Gson;

import main.controller.aops.AOPdo1;
import main.controller.aops.AOPdo2;
import main.frameWork.RenderBean;
import main.frameWork.RenderFactory;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.PathParam;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestHeader;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl {// implements NormalController
    @Autowired
    public EazyService service1;

    @Autowired
    public EazyServiceImpl service2;

    // @Autowired
    // public EazyService service2;

    @WebPath(methed = "GET", route = "/")
    public RenderBean doGet() throws IOException {
        try {
            String realPath = "D:\\learning\\main.html";

            byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());

            return RenderFactory.render("byte").setByte(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    @WebPath(methed = "GET", route = "/s1/vi")
    public RenderBean doVi() {
        String path = "s1\\vi.html";

        return RenderFactory.render("html").path(path);
    }

    @WebPath(methed = "GET", route = "/s1/jp")
    @AOP(AOPdo1.class)
    public RenderBean doGet2() {
        String path = "s1/jp.html";

        return RenderFactory.render("html").path(path);

    }

    @WebPath(methed = "POST", route = "/s1/req")
    @AOP(AOPdo1.class)
    public String doRequestBody(@RequestBody Apple apple, @RequestHeader Map map) throws IOException {
        try {
            String aa = "{\"name\":\"黑心貧果\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
            Gson gg = new Gson();
            System.out.println("LOVE~" + apple.name);
            System.out.println("LOVE~" + map);
            // getAppleFromDB();
            return aa;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @WebPath(methed = "POST", route = "/s1/req2")
    @AOP(AOPdo1.class)
    public Apple doRequestBody2() throws IOException {
        try {
            String aa = "{\"name\":\"黑心梨子\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
            Gson gg = new Gson();

            return gg.fromJson(aa, Apple.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @WebPath(methed = "GET", route = "/s1/sum2")
    @AOP(AOPdo2.class)
    public RenderBean doGet3(@RequestParamMap Map<String, String> map) {

        System.out.println("MAP:" + map);

        String path = "s1\\sum2.html";
        System.out.println("realPath:" + path);
        if (map != null) {
            int i1 = Integer.parseInt((String) map.get("numa"));
            int ib = Integer.parseInt((String) map.get("numb"));
            // System.out.println("??" + service1);
            // System.out.println("??" + this);
            int sum = service1.getXandY(i1, ib);

            int sub = service2.getXsybY(i1, ib);

            return RenderFactory.render("html").path(path)
                    .trans("SUM", "相加:" + sum)
                    .trans("SUB", "相減:" + sub);
        } else {
            return RenderFactory.render("html").path(path)
                    .trans("SUM", "")
                    .trans("SUB", "");
        }

    }

    @WebPath(methed = "GET", route = "/s1/coo")
    public RenderBean doGET_coo() {

        String path = "s1\\coo.html";

        return RenderFactory.render("html").path(path);

    }

    @WebPath(methed = "POST", route = "/s1/coo")
    public RenderBean doPOST_coo(@RequestParamMap Map<String, String> map) {

        System.out.println("MAP:" + map);

        String path = "s1\\coo.html";
        System.out.println("realPath:" + path);
        // TODO cookie
        // resp.cookie("yourName", req.getPostBodyMap().get("name") + "!!!");
        // resp.cookie("yourPasswd", req.getPostBodyMap().get("passwd") + "!!!");
        // resp.renderHtml(path);
        //
        return RenderFactory.render("html").path(path);

    }

    @WebPath(methed = "GET", route = "file")
    public RenderBean responsefile(@PathParam String path) throws IOException {
        System.out.println("--------" + path);

        return RenderFactory.render("file").path(path);
    }

}

class Apple {
    public String name = "";
    private String color = "";
    private String nike = "";
    int num = 123;

    public String getSs2() {
        return nike;
    }

    public void setSs2(String ss2) {
        this.nike = ss2;
    }

}
