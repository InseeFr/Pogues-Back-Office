package fr.insee.pogues.api.remote.metadata.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class TestAPI {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/* Disable certificats */


			    SSLContext sslcontext = null;
				try {
					sslcontext = SSLContext.getInstance("TLS");
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    try {
					sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
					    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
					    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }

					}}, new java.security.SecureRandom());
				} catch (KeyManagementException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    Client client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
		

			    String ddi = client.target("https://dvrmesgrepwas01.ad.insee.intra/api/v1/item/fr.insee/d3ed6638-583f-4ce0-b677-e31e6df89921?api_key=ADMINKEY")
			            .request(MediaType.APPLICATION_XML)
			            .get(String.class);
			    
			    System.out.println(ddi);
			    
			   
			    
			    
//		CloseableHttpClient httpclient = HttpClients.createDefault();
//		// HttpGet httpGet = new
//		// HttpGet("http://s90datalift.ad.insee.intra:9250/exist/restxq/questionnaire/fr.insee-POPO-QPO-CAM2017");
//		HttpGet httpGet = new HttpGet(
//				"https://dvrmesgrepwas01.ad.insee.intra/api/v1/item/fr.insee/d3ed6638-583f-4ce0-b677-e31e6df89921?api_key=ADMINKEY");
//
//		// HttpGet httpGet = new
//		// HttpGet("https://dvrmesgrepwas01.ad.insee.intra/api/v1/_query/relationship/bysubject/descriptions?api_key=ADMINKEY");
//
//		CloseableHttpResponse response1 = null;
//		try {
//			response1 = httpclient.execute(httpGet);
//			// The underlying HTTP connection is still held by the response
//			// object
//			// to allow the response content to be streamed directly from the
//			// network socket.
//			// In order to ensure correct deallocation of system resources
//			// the user MUST call CloseableHttpResponse#close() from a finally
//			// clause.
//			// Please note that if response content is not fully consumed the
//			// underlying
//			// connection cannot be safely re-used and will be shut down and
//			// discarded
//			// by the connection manager.
//			System.out.println(response1.getStatusLine());
//			HttpEntity entity1 = response1.getEntity();
//			// do something useful with the response body
//			// and ensure it is fully consumed
//
//			BufferedReader br = new BufferedReader(new InputStreamReader(entity1.getContent()));
//			// ici in.available() me renvoie bien le nombre de byte restant.
//			System.out.println(br.readLine());
//			EntityUtils.consume(entity1);
//
//		} catch (ClientProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				response1.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

		// HttpPost httpPost = new HttpPost("http://targethost/login");
		// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		// nvps.add(new BasicNameValuePair("username", "vip"));
		// nvps.add(new BasicNameValuePair("password", "secret"));
		// CloseableHttpResponse response2 = null;
		// try {
		// httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		// response2 = httpclient.execute(httpPost);
		// System.out.println(response2.getStatusLine());
		// HttpEntity entity2 = response2.getEntity();
		// // do something useful with the response body
		// // and ensure it is fully consumed
		// EntityUtils.consume(entity2);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (ClientProtocolException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } finally {
		// try {
		// response2.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

	}

}
