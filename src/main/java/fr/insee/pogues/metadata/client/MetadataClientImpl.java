package fr.insee.pogues.metadata.client;

import fr.insee.pogues.webservice.rest.PoguesException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetadataClientImpl implements MetadataClient {

    @Autowired
    HttpClientBuilder httpClientBuilder;

    @Value("${fr.insee.pogues.api.remote.metadata.url}")
    String serviceUrl;

    @Value("${fr.insee.pogues.api.remote.metadata.agency}")
    String agency;

    @Value("${fr.insee.pogues.api.remote.metadata.key}")
    String apiKey;


    @Override
    public JSONObject getItem(String id) throws Exception {
        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            String url = String.format("%s/api/v1/item/%s/%s?api_key=%s", serviceUrl, agency, id, apiKey);
            HttpGet get = new HttpGet(url);
            get.addHeader("accept", "application/json");
            HttpResponse response = httpClient.execute(get);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new PoguesException(response.getStatusLine().getStatusCode(),
                        "Colectica server error", response.getStatusLine().getReasonPhrase());
            }
            String body = EntityUtils.toString(response.getEntity());
            return (JSONObject)
                    new JSONParser().parse(body);
        }
    }
}
