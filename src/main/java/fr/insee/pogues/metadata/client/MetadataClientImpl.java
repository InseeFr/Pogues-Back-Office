package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MetadataClientImpl implements MetadataClient {

    private static final Logger logger = LogManager.getLogger(MetadataClientImpl.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${fr.insee.pogues.api.remote.metadata.url}")
    String serviceUrl;

    @Value("${fr.insee.pogues.api.remote.metadata.agency}")
    String agency;

    @Value("${fr.insee.pogues.api.remote.metadata.key}")
    String apiKey;

    public ColecticaItem getItem(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s", serviceUrl, id);
        return restTemplate.getForObject(url, ColecticaItem.class);
    }

    public List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception {
        String url = String.format("%s/meta-data/items", serviceUrl);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-type", ContentType.APPLICATION_JSON.getMimeType());
        HttpEntity<ColecticaItemRefList> request = new HttpEntity<>(query, headers);
        ResponseEntity<ColecticaItem[]> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ColecticaItem[].class);
        return Arrays.asList(response.getBody());
    }

    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s/refs", serviceUrl, id);
        System.out.println(url);
        ResponseEntity<ColecticaItemRefList>  response;
        response = restTemplate
                .exchange(url, HttpMethod.GET, null, ColecticaItemRefList.class);
        return response.getBody();
    }

    public String getDDIDocument(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s/ddi", serviceUrl, id);
        ResponseEntity<String>  response;
        response = restTemplate
                .exchange(url, HttpMethod.GET, null, String.class);
        return response.getBody();
    }
    
	public List<Unit> getUnits() throws Exception {
		String url = String.format("%s/meta-data/units", serviceUrl);
		ResponseEntity<Unit[]> response = restTemplate.exchange(url, HttpMethod.GET, null, Unit[].class);
		return Arrays.asList(response.getBody());
	}

	@Override
	public String getCodeList(String id) throws Exception {
		String url = String.format("%s/meta-data/codeList/%s/ddi", serviceUrl, id);
		ResponseEntity<String> response;
		response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		return response.getBody();
	}
    
    
}
