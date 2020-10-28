package main.frameWork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import main.frameWork.annotatoins.PathParam;
import main.frameWork.annotatoins.RequestBody;
import main.frameWork.annotatoins.RequestHeader;
import main.frameWork.annotatoins.RequestParamMap;
import main.frameWork.beans.HttpRequest;
import main.frameWork.beans.HttpResponse;
import main.frameWork.beans.MethodsWithObjs;

public class ControllerInvoker {
    Gson gson = new Gson();

    public void invokeToController(HttpRequest htmlRequest, HttpResponse httpResponse) throws Exception {

        if (htmlRequest.isWebsocket()) {// http + websocket
            new WebSocketHandler(htmlRequest, Resources.annotationMap, htmlRequest.getRequestURI()).all();
            httpResponse.setWebSicket(true);
        } else {// http
            invokeToHttpController(htmlRequest, httpResponse);
        }

    }

    private void invokeToHttpController(HttpRequest htmlRequest, HttpResponse httpResponse) throws Exception {
        MethodsWithObjs methodObj = getMethodbyAnnotation(htmlRequest);
        if (methodObj == null) {

            throw new MyHTTPException("invokeToController: get no method to invoke, check route");
        }

        Object[] inParas = makeHttpInvokeParas(methodObj, htmlRequest);

        Object objToInvoke = null;
        Method methodToInvoke = null;
        Object resObj = null;
        if (methodObj.isProxyed()) {
            objToInvoke = methodObj.getProxyObj();
            methodToInvoke = methodObj.getProxyMethod();
        } else {
            objToInvoke = methodObj.getRealObj();
            methodToInvoke = methodObj.getRealMethod();
        }

        resObj = methodToInvoke.invoke(objToInvoke, inParas);

        if (resObj != null) {
            afterHttpInvoke(resObj, httpResponse);
        } else {
            throw new MyHTTPException("not expect return:" + resObj);
        }

    }

    private Object[] makeHttpInvokeParas(MethodsWithObjs methodObj, HttpRequest htmlRequest) throws MyHTTPException {
        AnnotatedType[] annotatedType = methodObj.getRealMethod().getAnnotatedParameterTypes();
        Annotation[][] annotations = methodObj.getRealMethod().getParameterAnnotations();
        System.out.println("annotatedType.length" + annotatedType.length);
        Object[] inParas = new Object[annotatedType.length];
        for (int i = 0; i < annotatedType.length; i++) {
            Annotation[] ann = annotations[i];
            if (ann.length > 0) {
                System.out.println(ann[0].annotationType());
                if (ann[0].annotationType() == RequestBody.class) {// json
                    Object reqBodyObj = gson.fromJson(htmlRequest.getRawPostBody(), annotatedType[i].getType());
                    inParas[i] = reqBodyObj;

                } else if (ann[0].annotationType() == RequestHeader.class) {
                    inParas[i] = (Map<String, String>) htmlRequest.getHttpHeaderMap();

                } else if (ann[0].annotationType() == RequestParamMap.class) {// http url paras and body paras

                    Map<String, String> map = new HashMap<>();
                    if (htmlRequest.getURLParameterMap() != null) {
                        System.out.println("!! " + htmlRequest.getURLParameterMap());
                        map.putAll(htmlRequest.getURLParameterMap());
                    }
                    if (htmlRequest.getPostBodyMap() != null) {
                        System.out.println("!! " + htmlRequest.getPostBodyMap());
                        map.putAll(htmlRequest.getPostBodyMap());
                    }
                    inParas[i] = map;
                } else if (ann[0].annotationType() == PathParam.class) {
                    inParas[i] = (String) htmlRequest.getRequestURI();

                }
            } else {
                throw new MyHTTPException("cannot find avaliable annotations for each paras");
            }

        }

        return inParas;
    }

    private void afterHttpInvoke(Object resObj, HttpResponse httpResponse) throws IOException {
        if (resObj.getClass() == String.class) {
            httpResponse.setResponseString((String) resObj);
        } else if (resObj.getClass() == RenderBean.class) {
            RenderBean renderBean = (RenderBean) resObj;

            if (renderBean.getType().toLowerCase().equals("byte")) {
                byte[] d = renderBean.getData();
                httpResponse.setResponseData(d);
            } else if (renderBean.getType().toLowerCase().equals("html")) {

                byte[] d = embeddedHtmlToDataByte(renderBean);
                httpResponse.setResponseData(d);
                if (!renderBean.getCookiesMap().isEmpty()) {
                    httpResponse.setcookieMap(renderBean.getCookiesMap());
                }

            } else if (renderBean.getType().toLowerCase().equals("file")) {

                byte[] d = readFileToDataByte(renderBean);
                httpResponse.setResponseData(d);
            }

        } else {// 其他類別自動轉成json
            String resString = gson.toJson(resObj);
            httpResponse.setResponseString(resString);
        }
    }

    private byte[] embeddedHtmlToDataByte(RenderBean renderBean) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        String realPath = Resources.whereMainAt + "resource/web/" + renderBean.getPath();
        BufferedReader in = new BufferedReader(new FileReader(realPath));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str + "\r\n");
        }
        in.close();

        String content = contentBuilder.toString();
        String keyTag = "";
        for (String[] aa : renderBean.getTransList()) {
            keyTag = "[j_start," + aa[0] + ",j_end]";
            content = content.replace(keyTag, aa[1]);
        }

        return content.getBytes();
    }

    private byte[] readFileToDataByte(RenderBean renderBean) throws IOException {
        String realPath = Resources.whereMainAt + "resource/web/" + renderBean.getPath();
        return Files.readAllBytes(new File(realPath).toPath());
    }

    private MethodsWithObjs getMethodbyAnnotation(HttpRequest htmlRequest) throws Exception {
        // System.out.println("!!!!" + htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());
        // MethodwithInvokeObj aa = annotationMap.get(htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());
        MethodsWithObjs methodsWithObjs = Resources.annotationMap.get(htmlRequest.getHttpMethod() + "_" + htmlRequest.getRequestURI());

        if (methodsWithObjs != null) {
            return methodsWithObjs;
        } else {

            if (htmlRequest.getHttpHeaderMap().get("Sec-Fetch-Dest").startsWith("image")) {
                return Resources.annotationMap.get("GET_file");

            } else {
                return null;
            }
        }

    }

}
