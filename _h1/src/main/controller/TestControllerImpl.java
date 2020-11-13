/**
 * 
 */
package main.controller;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;

import main.frameWork.annotatoins.Autowired;
import main.frameWork.annotatoins.Controller;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.annotatoins.WebPath;

@Controller
public class TestControllerImpl {// implements NormalController
    @Autowired
    EazyService service1;

    @Autowired
    EazyServiceImpl service2;

    private String s1 = "A_private_String";
    public String s2 = "A_public_String";

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

    @WebPath(methed = "POST", route = "/test/apple")
    public TestApple testApple(@RequestBody TestApple apple) throws IOException {

        return apple;

    }

}

class TestApple {
    private boolean isWarmed = true;
    public String name = "測試蟲蘋果";

}
