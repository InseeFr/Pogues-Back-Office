package fr.insee.pogues.api.remote.eno.transforms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EnoClientImpl implements EnoClient{
	
	private static final Logger logger = LogManager.getLogger(EnoClientImpl.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${fr.insee.pogues.api.remote.eno.host}")
    static String enoHost;
	
	static final String SCHEME = "http";
	
	@Override
	public String getDDI32ToDDI33 (File fileInput) throws Exception{
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/ddi32-2-ddi33");
        return EntityUtils.toString(entityResponse, "UTF-8");
	};
	
	@Override
	public String getDDIToODT (File fileInput) throws Exception{
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/odt");
        return EntityUtils.toString(entityResponse, "UTF-8");
	};
	
	@Override
	public String getXMLPoguesToDDI (File fileInput) throws Exception{
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/xmlpogues-2-ddi");
        return EntityUtils.toString(entityResponse, "UTF-8");
	};
	
	@Override
	public String getDDIToPDF (File fileInput) throws URISyntaxException, ClientProtocolException, IOException{
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme(SCHEME).setHost(enoHost).setPath("/questionnaire/DEFAULT/pdf");
		
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		HttpPost post = new HttpPost(uriBuilder.build());
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("in", fileInput, ContentType.DEFAULT_BINARY, fileInput.getName());
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		HttpResponse response = httpclient.execute(post);
		HttpEntity entityResponse = response.getEntity();
		String outFilePath = FilenameUtils.removeExtension(fileInput.getPath()) + ".pdf";
		logger.debug("Output file : " + outFilePath);
		File outFilePDF = new File(outFilePath);	
		if (entityResponse != null) {
		    InputStream inputStream = entityResponse.getContent();
		    OutputStream outputStream = new FileOutputStream(outFilePDF);
		    IOUtils.copy(inputStream, outputStream);
		    outputStream.close();
		}
        return outFilePDF.getAbsolutePath();
	};
	
	@Override
	public String getDDIToFO(File fileInput) throws URISyntaxException, ClientProtocolException, IOException {
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/DEFAULT/fo");
        return EntityUtils.toString(entityResponse, "UTF-8");
	}
	
	@Override
	public String getDDITOLunaticXML(File fileInput) throws URISyntaxException, ClientProtocolException, IOException {
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/DEFAULT/xml-lunatic");
        return EntityUtils.toString(entityResponse, "UTF-8");
	}
	
	@Override
	public String getDDITOLunaticJSON(File fileInput) throws URISyntaxException, ClientProtocolException, IOException {
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/DEFAULT/json-lunatic");
        return EntityUtils.toString(entityResponse, "UTF-8");
	}
	
	@Override
	public String getDDITOXForms(File fileInput) throws URISyntaxException, ClientProtocolException, IOException {
		HttpEntity entityResponse = callEnoApi(fileInput, "/questionnaire/DEFAULT/xforms");
        return EntityUtils.toString(entityResponse, "UTF-8");
	}
	
	@Override
	public void getParameters () throws Exception{
		
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme(SCHEME).setHost(enoHost).setPath("/parameter/default");
		
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> result = restTemplate.exchange(uriBuilder.build(), HttpMethod.GET, null, String.class);
	    System.out.println(result.getBody());
	};
	
	static private HttpEntity callEnoApi(File fileInput, String WSPath) throws URISyntaxException, ClientProtocolException, IOException {
		URIBuilder uriBuilder = new URIBuilder();
		uriBuilder.setScheme(SCHEME).setHost(enoHost).setPath(WSPath);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();		
		HttpPost post = new HttpPost(uriBuilder.build());
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("in", fileInput, ContentType.DEFAULT_BINARY, fileInput.getName());
		HttpEntity entity = builder.build();
		post.setEntity(entity);
		HttpResponse response = httpclient.execute(post);
		return response.getEntity();
	}


}

