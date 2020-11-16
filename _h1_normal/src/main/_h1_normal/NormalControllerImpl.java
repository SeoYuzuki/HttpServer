package main._h1_normal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import main.controller.aops.AOPdo1;
import main.controller.aops.AOPdo2;
import main.controller.aops.AOPdo3;
import main.frameWork.RenderBean;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl {// implements NormalController
    @Autowired
    EazyService service1;

    @Autowired
    EazyServiceImpl service2;

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

    // @WebPath(methed = "GET", route = "file")
    // public RenderBean responsefile(@PathParam String path) throws IOException {
    // System.out.println("--------" + path);
    //
    // return RenderFactory.render("file").path(path);
    // }

}
