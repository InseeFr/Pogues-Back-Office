package fr.insee.pogues.metadata.client;

import fr.insee.pogues.configuration.RemoteMetadata;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

// TODO: change for webclient
@Service
public class MetadataClientImpl implements MetadataClient {

	private RemoteMetadata remoteMetadata;

    private RestTemplate restTemplate;

	protected MetadataClientImpl(){}

	@Autowired
	public MetadataClientImpl(RemoteMetadata remoteMetadata, RestTemplateBuilder restTemplateBuilder) {
		this.remoteMetadata = remoteMetadata;
		this.restTemplate=restTemplateBuilder.build();
	}

	public ColecticaItem getItem(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s", remoteMetadata.getUrl(), id);
        return restTemplate.getForObject(url, ColecticaItem.class);
    }

    public List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception {
        String url = String.format("%s/meta-data/items", remoteMetadata.getUrl());
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<ColecticaItemRefList> request = new HttpEntity<>(query, headers);
        ResponseEntity<ColecticaItem[]> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ColecticaItem[].class);
        return Arrays.asList(response.getBody());
    }

    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s/refs", remoteMetadata.getUrl(), id);
        System.out.println(url);
        ResponseEntity<ColecticaItemRefList>  response;
        response = restTemplate
                .exchange(url, HttpMethod.GET, null, ColecticaItemRefList.class);
        return response.getBody();
    }

    public String getDDIDocument(String id) throws Exception {
        String url = String.format("%s/meta-data/item/%s/ddi", remoteMetadata.getUrl(), id);
        ResponseEntity<String>  response;
        response = restTemplate
                .exchange(url, HttpMethod.GET, null, String.class);
        return response.getBody();
    }
    
	public List<Unit> getUnits() throws Exception {
		String url = String.format("%s/meta-data/units", remoteMetadata.getUrl());
		ResponseEntity<Unit[]> response = restTemplate.exchange(url, HttpMethod.GET, null, Unit[].class);
		return Arrays.asList(response.getBody());
	}

	@Override
	public String getCodeList(String id) throws Exception {
		String url = String.format("%s/meta-data/codeList/%s/ddi", remoteMetadata.getUrl(), id);
		ResponseEntity<String> response;
		response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		return response.getBody();
	}
    
    
}
