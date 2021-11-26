package fr.insee.pogues.transforms.visualize;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class XFormsToURIStromaeV1Impl implements XFormsToURIStromaeV1 {
    
    @Autowired
    HttpClientBuilder httpClientBuilder;

    @Value("${fr.insee.pogues.api.remote.stromae.host}")
    private String serviceUriHost;
    
    @Value("${fr.insee.pogues.api.remote.stromae.vis.path}")
    private String serviceUriVisualizationPath;

    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
    	try(CloseableHttpClient httpClient = httpClientBuilder.build()) {
            String uri = String.format("%s/%s/%s/%s", serviceUriHost, serviceUriVisualizationPath,
                    params.get("dataCollection"),params.get("questionnaire"));
            HttpPost post = new HttpPost(uri);
            post.setEntity(new StringEntity(input, StandardCharsets.UTF_8));
            post.setHeader("Content-type", "application/xml");
            HttpResponse response = httpClient.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            throw new Exception(String.format("%s:%s", getClass().getName(), e.getMessage()));
        }
    }
}
