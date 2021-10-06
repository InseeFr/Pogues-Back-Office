package fr.insee.pogues.metadata.client;

import fr.insee.pogues.config.RemoteMetadata;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        headers.add("Content-type", ContentType.APPLICATION_JSON.getMimeType());
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
        
    	// Fake
    	List<Unit> units = new ArrayList<>();
    	Unit unit1 =new Unit();
    	unit1.setLabel("€");
    	unit1.setUri("http://id.insee.fr/unit/euro");
    	units.add(unit1);
    	Unit unit2 =new Unit();
    	unit2.setLabel("k€");
    	unit2.setUri("http://id.insee.fr/unit/keuro");
    	units.add(unit2);
    	Unit unit3 =new Unit();
    	unit3.setLabel("%");
    	unit3.setUri("http://id.insee.fr/unit/percent");
    	units.add(unit3);
    	Unit unit4 =new Unit();
    	unit4.setLabel("heures");
    	unit4.setUri("http://id.insee.fr/unit/heure");
    	units.add(unit4);
    	Unit unit5 =new Unit();
    	unit5.setLabel("jours");
    	unit5.setUri("http://id.insee.fr/unit/jour");
    	units.add(unit5);
    	Unit unit6 =new Unit();
    	unit6.setLabel("semaine");
    	unit6.setUri("http://id.insee.fr/unit/semaine");
    	units.add(unit6);
    	Unit unit7 =new Unit();
    	unit7.setLabel("mois");
    	unit7.setUri("http://id.insee.fr/unit/mois");
    	units.add(unit7);
    	Unit unit8 =new Unit();
    	unit8.setLabel("années");
    	unit8.setUri("http://id.insee.fr/unit/annee");
    	units.add(unit8);
    	Unit unit9 =new Unit();
    	unit9.setLabel("m");
    	unit9.setUri("http://id.insee.fr/unit/metre");
    	units.add(unit9);
    	Unit unit10 =new Unit();
    	unit10.setLabel("m2");
    	unit10.setUri("http://id.insee.fr/unit/metrecarre");
    	units.add(unit10);
    	
        return units;
    }

	@Override
	public String getCodeList(String id) throws Exception {
		 String url = String.format("%s/meta-data/codeList/%s/ddi", remoteMetadata.getUrl(), id);
	        ResponseEntity<String>  response;
	        response = restTemplate
	                .exchange(url, HttpMethod.GET, null, String.class);
	        return response.getBody();
	}
    
    
}
