/**
 * 
 */
package main._h1_normal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import main._h1_normal.cusAOPs.AOPdo1;
import main._h1_normal.cusAOPs.AOPdo2;
import main._h1_normal.cusAOPs.AOPdo3;
import main.frameWork.RenderBean;
import main.frameWork.RenderFactory;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Async;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.PathParam;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
// @AOP(message = AOPdo1.class)
public class NormalControllerImpl {
    @Autowired
    EazyService service1;

    @Autowired
    EazyServiceImpl service2;

    @WebPath(methed = "GET", route = "/")
    public RenderBean doGet(@PathParam String path) throws IOException {

        // byte[] fileContent = Files.readAllBytes(new File(realPath).toPath());
        // return new RenderBean("byte").setByte(fileContent);
        return RenderFactory.render("html").path(path + "main.html");

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

    @WebPath(methed = "GET", route = "/testV")
    public Apple testV() {

        return new Apple();

    }

    @WebPath(methed = "OPTIONS", route = "/testV")
    public Apple testV2() {

        return new Apple();

    }

    @WebPath(methed = "POST", route = "/testV")
    public Apple testV3(@RequestBody SSSABC SSSABC) throws InterruptedException, ExecutionException {

        CompletableFuture<String> cf = asyncRe(SSSABC.waitFor);
        cf.get();

        return new Apple();

    }

    @Async
    public CompletableFuture<String> asyncRe(String waitFor) {
        try {
            Thread.sleep(Integer.parseInt(waitFor));
            System.out.println("~yo 5s");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture("asyncTest2 completed");
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

class Apple {
    String isSaveSuccessfully = "1";

}

class SSSABC {
    String waitFor = "0";
}
