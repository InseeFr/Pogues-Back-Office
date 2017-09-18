package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRef;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String url = String.format("%s/api/v1/item/%s/%s?api_key=%s", serviceUrl, agency, id, apiKey);
        logger.debug("xxx: " + url);
        return restTemplate.getForObject(url, ColecticaItem.class);
    }

    public List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception {
        String url = String.format("%s/api/v1/item/_getList?api_key=%s", serviceUrl, apiKey);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-type", ContentType.APPLICATION_JSON.getMimeType());
        HttpEntity<ColecticaItemRefList> request = new HttpEntity<>(query, headers);
        ResponseEntity<ColecticaItem[]> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ColecticaItem[].class);
        return Arrays.asList(response.getBody());
    }

    public ColecticaItemRefList getChildrenRef(String id) throws Exception {
        String url = String.format("%s/api/v1/set/%s/%s?api_key=%s", serviceUrl, agency, id, apiKey);
        ResponseEntity<ColecticaItemRef.Unformatted[]>  response;
        response = restTemplate
                .exchange(url, HttpMethod.GET, null, ColecticaItemRef.Unformatted[].class);
        List<ColecticaItemRef> refs = Arrays.asList(response.getBody())
                .stream()
                .map(unformatted -> unformatted.format())
                .collect(Collectors.toList());
        return new ColecticaItemRefList(refs);
    }
}
