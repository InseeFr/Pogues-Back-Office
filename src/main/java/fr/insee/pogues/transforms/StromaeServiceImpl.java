package fr.insee.pogues.transforms;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class StromaeServiceImpl implements StromaeService {

    @Autowired
    HttpClientBuilder httpClientBuilder;

    @Value("${fr.insee.pogues.api.remote.stromae.db.host}")
    private String stromaeHost;
    
    @Value("${fr.insee.pogues.api.remote.stromae.db.port}")
    private String stromaePort;
    
    @Value("${fr.insee.pogues.api.remote.stromae.vis.url}")
    private String visuUri;

    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        try(CloseableHttpClient httpClient = httpClientBuilder.build()) {
            String uri = String.format("%s:%s/%s/%s/%s", stromaeHost, stromaePort, visuUri,
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
