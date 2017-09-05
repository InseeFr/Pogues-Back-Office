package fr.insee.pogues.metadata.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetadataClientImpl implements MetadataClient {

    @Autowired
    HttpClient httpClient;

    @Value("${fr.insee.pogues.api.remote.metadata.url}")
    String serviceUrl;

    @Override
    public JSONObject getItem(String id) throws Exception{
        try {
            HttpGet get = new HttpGet(String.format("%s/api/v1/item/fr.insee/%s/1?api_key=ADMINKEY", serviceUrl, id));
            get.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            String body = EntityUtils.toString(response.getEntity());
            return (JSONObject)
                        new JSONParser().parse(body);
        } catch (Exception e) {
            throw e;
        }
    }
}
