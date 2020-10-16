/**
 * 
 */
package HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

import HttpServer.beans.HttpResponse;

public class RenderModel {

    public byte[] toRender(HttpResponse httpResponse) throws MyHTTPException, IOException {
        if (httpResponse.getRenderWhat().equals("Html")) {
            return embeddedHtmlToDataByte(httpResponse);
        } else if (httpResponse.getRenderWhat().equals("File")) {
            return readFileToDataByte(httpResponse);
        } else {
            throw new MyHTTPException("RenderModel, cannot find render ");
        }

    }

    private byte[] embeddedHtmlToDataByte(HttpResponse httpResponse) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        BufferedReader in = new BufferedReader(new FileReader(httpResponse.getRealPath()));
        String str;
        while ((str = in.readLine()) != null) {
            contentBuilder.append(str);
        }
        in.close();

        String content = contentBuilder.toString();
        String keyTag = "";
        for (String[] aa : httpResponse.getList()) {
            keyTag = "[j_start," + aa[0] + ",j_end]";
            content = content.replace(keyTag, aa[1]);
        }

        return content.getBytes();
    }

    private byte[] readFileToDataByte(HttpResponse httpResponse) throws IOException {
        return Files.readAllBytes(new File(httpResponse.getRealPath()).toPath());
    }
}
