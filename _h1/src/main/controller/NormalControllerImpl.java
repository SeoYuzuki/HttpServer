/**
 * 
 */
package main.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.google.gson.Gson;

import main.controller.aops.AOPdo1;
import main.controller.aops.AOPdo2;
import main.controller.aops.AOPdo3;
import main.frameWork.RenderBean;
import main.frameWork.RenderFactory;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.PathParam;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl {// implements NormalController
    @Autowired
    public EazyService service1;

    @Autowired
    public EazyServiceImpl service2;

    @WebPath(methed = "GET", route = "/")
    public RenderBean doGet() throws IOException {
        try {
            String realPath = "C:\\Users\\ESB20663\\git\\HttpServer\\_h1\\src\\main\\resource\\web\\main.html";

            byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());

            return new RenderBean("byte").setByte(fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    @WebPath(methed = "GET", route = "/s1/vi")
    @AOP(AOPdo3.class)
    public RenderBean doVi() {
        String path = "s1\\vi.html";

        return new RenderBean("html").path(path);
    }

    @WebPath(methed = "GET", route = "/s1/jp")
    @AOP(AOPdo1.class)
    public RenderBean doGet2() {
        String path = "s1/jp.html";

        return new RenderBean("html").path(path);

    }

    @WebPath(methed = "POST", route = "/s1/req")
    @AOP(AOPdo1.class)
    public String doRequestBody(@RequestBody Apple apple) throws IOException {
        try {
            String aa = "{\"name\":\"黑心貧果\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";

            System.out.println("LOVE~" + apple.name);

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
        if (!map.isEmpty()) {
            int i1 = Integer.parseInt((String) map.get("numa"));
            int ib = Integer.parseInt((String) map.get("numb"));
            // System.out.println("??" + service1);
            // System.out.println("??" + this);
            int sum = service1.getXandY(i1, ib);

            int sub = service2.getXsubY(i1, ib);

            return new RenderBean("html").path(path)
                    .trans("SUM", "相加:" + sum)
                    .trans("SUB", "相減:" + sub);
        } else {
            return new RenderBean("html").path(path)
                    .trans("SUM", "")
                    .trans("SUB", "");
        }

    }

    @WebPath(methed = "GET", route = "/s1/coo")
    public RenderBean doGET_coo() {

        String path = "s1\\coo.html";

        return new RenderBean("html").path(path);

    }

    @WebPath(methed = "POST", route = "/s1/coo")
    public RenderBean doPOST_coo(@RequestParamMap Map<String, String> map) {

        @WebPath(methed = "POST", route = "/s1/coo")
        String path = "s1\\coo.html";
        System.out.println("map:" + map);

        return new RenderBean("html").path(path)
                .addCookie("yourName", map.get("name") + "!!!")
                .addCookie("yourPasswd", map.get("passwd") + "!!!");

    }

    @WebPath(methed = "GET", route = "/err1")
    public void testerr1() throws Exception {
        throw new Exception("hi");
    }

    @WebPath(methed = "GET", route = "/err2")
    @AOP(AOPdo1.class)
    public void testerr2() throws Exception {
        throw new Exception("hi");
    }

    @WebPath(methed = "GET", route = "file")
    public RenderBean responsefile(@PathParam String path) throws IOException {
        System.out.println("--------" + path);

        return RenderFactory.render("file").path(path);
    }

    @WebPath(methed = "GET", route = "/async")
    public String asynctest() throws IOException {

        service2.asyncTest();
        return "{\"name\":\"平行梨子\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
    }

    @WebPath(methed = "GET", route = "/async2")
    public String asynctest2() throws IOException {
        System.out.println("asynctest2_1");
        CompletableFuture<String> cf = service2.asyncTest2();
        System.out.println("asynctest2_2");
        try {
            System.out.println("get!!: " + cf.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"name\":\"平行百香果\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
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
