/**
 * 
 */
package main.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import main.controller.aops.AOPtest1;
import main.controller.aops.AOPtest2;
import main.controller.aops.AOPtest3;
import main.controller.aops.AOPtest4;
import main.frameWork.RenderBean;
import main.frameWork.RenderFactory;
import main.frameWork.Resources;
import main.frameWork.annotatoins.AOP;
import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.PathParam;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
public class TestControllerImpl {// implements NormalController
    @Autowired
    EazyService service1;

    @Autowired
    EazyServiceImpl service2;

    @WebPath(methed = "GET", route = "/test")
    public RenderBean testGET(@PathParam String path) throws IOException {
        return RenderFactory.render("html").path(path + ".html");

    }

    @WebPath(methed = "GET", route = "/test/String1")
    public String testGET_String1() throws IOException {
        return "String1";

    }

    @WebPath(methed = "POST", route = "/test/String1")
    public String testPOST_String1() throws Exception {

        return "String1";
    }

    @WebPath(methed = "GET", route = "/test/String2")
    public String testGET_String2(@RequestParamMap Map<String, String> map) throws IOException {
        // System.out.println("map:" + map);
        return map.toString();
    }

    @WebPath(methed = "POST", route = "/test/String2")
    public String testPOST_String2(@RequestParamMap Map<String, String> map) throws IOException {
        // System.out.println("map:" + map);
        return map.toString();
    }

    @WebPath(methed = "GET", route = "/test/String3")
    public String testString3(@RequestParamMap Map<String, String> map) throws IOException {

        int i1 = Integer.parseInt((String) map.get("numa"));
        int ib = Integer.parseInt((String) map.get("numb"));
        // System.out.println("??" + service1);
        // System.out.println("??" + this);
        int sum = service1.getXandY(i1, ib);

        int sub = service2.getXsubY(i1, ib);
        return String.valueOf(sum) + " " + String.valueOf(sub);

    }

    /**
     * 測試輸入輸出自動轉型
     */
    @WebPath(methed = "POST", route = "/test/apple")
    public TestApple testApple(@RequestBody TestApple apple) throws IOException {

        return apple;

    }

    /**
     * 測試嵌入式js
     */
    @WebPath(methed = "GET", route = "/test/emjs")
    @AOP(AOPtest2.class)
    public Map<String, String> doGet3(@RequestParamMap Map<String, String> map) {
        return map;

    }

    /**
     * 測試非同步註解 無等待return
     */
    @WebPath(methed = "GET", route = "/test/async")
    public String asynctest() throws IOException {

        service2.asyncTest();
        return "{\"name\":\"平行梨子\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
    }

    /**
     * 測試非同步註解 等待future return
     */
    @WebPath(methed = "GET", route = "/test/async2")
    String asynctest2() throws Exception {
        System.out.println("asynctest2_1");
        CompletableFuture<String> cf = service2.asyncTest2();

        System.out.println("asynctest2_2");

        return "{\"name\":\"平行百香果\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
    }

    @WebPath(methed = "GET", route = "/test/err1")
    public void testerr1() throws Exception {
        throw new Exception("hi");
    }

    /**
     * 測試有AOP的例外
     */
    @WebPath(methed = "GET", route = "/test/err2")
    @AOP(AOPtest1.class)
    public void testerr2() throws Exception {

        throw new Exception("hi");

    }

    /**
     * 測試內部AOP互call是否生效
     */
    @WebPath(methed = "GET", route = "/test/cp")
    @AOP(AOPtest1.class)
    String testcurrentProxy() {
        TestControllerImpl oo = (TestControllerImpl) Resources.currentProxy.get();
        oo.doNothing();
        // doNothing();

        return "{\"name\":\"咬尾蛇蘋果\",\"color\":\"green\",\"nike\":\"\",\"num\":123}";
    }

    @AOP(AOPtest3.class)
    public int doNothing() {
        doNothing2();
        return 0;

    }

    @AOP(AOPtest4.class)
    public int doNothing2() {
        return 0;

    }
}

class TestApple {
    private boolean isWarmed = true;
    public String name = "測試蟲蘋果";

}
