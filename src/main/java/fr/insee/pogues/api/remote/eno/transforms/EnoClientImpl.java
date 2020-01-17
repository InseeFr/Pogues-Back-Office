package fr.insee.pogues.api.remote.eno.transforms;

import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class EnoClientImpl implements EnoClient {
	
	@Autowired
    RestTemplate restTemplate;
	
	String serviceEnoUrl = "https://ddi33-eno-ws-sic.dev.innovation.insee.eu";
	
	public String getDDI33FromDDI32 (String ddi32) throws Exception{
//		String url = String.format("%s/questionnaire/ddi32-2-ddi33", serviceEnoUrl);
//        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
//        headers.add("Content-type", ContentType.MULTIPART_FORM_DATA.getMimeType());
//        HttpEntity<String> request = new HttpEntity<>(ddi32, headers);
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//		return response.getBody();
		return null;
	};

}
