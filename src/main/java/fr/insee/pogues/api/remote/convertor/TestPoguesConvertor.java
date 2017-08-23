package fr.insee.pogues.api.remote.convertor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;




public class TestPoguesConvertor {

	public static void main(String[] args) {

				
		CloseableHttpClient httpclient = HttpClients.createDefault();
	
		HttpPost httpPost = new HttpPost("http://s90datalift.ad.insee.intra:8780/pogues-model/questionnaire/xml-json");
		httpPost.setHeader("Content-type", MediaType.APPLICATION_XML);
		
		BufferedReader buf;
		CloseableHttpResponse response1 = null;
		try {
			buf = new BufferedReader(new FileReader("src/test/resources/questionnaire-pogues-model.xml"));
			String         line = null;
			StringBuilder  stringBuilder = new StringBuilder();
		    String         ls = System.getProperty("line.separator");
		    
		    while((line = buf.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }
		    
			String ddi = stringBuilder.toString();
	        HttpEntity entity = new ByteArrayEntity(ddi.getBytes("UTF-8"));
			httpPost.setEntity(entity);
			
			response1 = httpclient.execute(httpPost);
			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			
			BufferedReader br = new BufferedReader(new InputStreamReader(entity1.getContent()));
			// ici in.available() me renvoie bien le nombre de byte restant.
			
			stringBuilder = new StringBuilder();
			while((line = br.readLine()) != null) {
	            stringBuilder.append(line);
	            stringBuilder.append(ls);
	        }
			
			System.out.println(stringBuilder.toString());
			// Ensures that the entity content is fully consumed and the content stream, if exists is closed.
			EntityUtils.consume(entity1);
			response1.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				response1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
