package fr.insee.pogues.transforms;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class StromaeServiceImpl implements StromaeService {

    @Autowired
    Environment env;

    private String serviceUri;

    @PostConstruct
    public void setUp(){
        serviceUri = env.getProperty("fr.insee.pogues.api.remote.stromae.vis.url");
    }
    @Override
    public String transform(String input, Map<String, Object> params) throws Exception {
        try {
            String uri = String.format("%s/%s", serviceUri,
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
