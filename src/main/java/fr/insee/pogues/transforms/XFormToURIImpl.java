package fr.insee.pogues.transforms;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class XFormToURIImpl implements XFormToURI {


    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params) throws Exception {

    }

    @Override
    public String transform(InputStream input, Map<String, Object> params) throws Exception {
        return null;
    }

    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        try {
            String uri = String.format("http://dvstromaeldb01.ad.insee.intra:8080/exist/restxq/visualize/%s",
                    params.get("name"));
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(input, StandardCharsets.UTF_8));
            post.setHeader("Content-type", "application/xml");
            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch(Exception e) {
            throw e;
        }
    }
}
