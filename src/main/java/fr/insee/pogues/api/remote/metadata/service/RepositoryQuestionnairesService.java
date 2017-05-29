package fr.insee.pogues.api.remote.metadata.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import fr.insee.pogues.api.remote.convertor.PoguesConvertorService;
import fr.insee.pogues.utils.ddi.DDIFunctions;

/**
 * Repository Questionnaire Service to assume the link with the metadata repository
 * 
 * @author I6VWID
 * 
 *
 */
public class RepositoryQuestionnairesService {


	final static Logger logger = Logger.getLogger(RepositoryQuestionnairesService.class);
	
	/**
	 * Contructor for Repository Questionnaires Service
	 * 
	 */
	public RepositoryQuestionnairesService() {
		
	}
	
	
	/**
	 * Main function to perform the DDI model of a questionnaire from id
	 * @return 
	 * 
	 */
	public String getDDIQuestionnaireByID(String id) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {

		String ddi = null; 
		
		// Step 1 - get the DDI instrument	
		logger.debug("Step 1 - get the DDI instrument");
		String instrumentDDIFragment = this.getDDIItemByID(id);
		
		// Step 1 bis - extract the Sequences List ID
		logger.debug("Step 1 bis - extract the Sequences List ID");
		Map<String,String> sequenceListID = DDIFunctions.getDDIControlConstructReferenceListFromFragment(instrumentDDIFragment);
				
		// Step 2 - for each seq, get the ddi fragment of the sub-seq
		logger.debug("Step 2 - for each seq, get the ddi fragment of the sub-seq");
		Map<String,String> sequenceListDDIFragment = new HashMap<String,String>(); 
		sequenceListID.forEach((key, value) -> {
			logger.debug(key);
			String sequenceFragment = this.getDDIItemByID(key);
			sequenceListDDIFragment.put(key, sequenceFragment);
			Map<String,String> subSequenceListID = DDIFunctions.getDDIControlConstructReferenceListFromSequenceFragment(sequenceFragment);
			// Step 3 - for each sub-seq, get the ddi fragment of the sub-sub-seq
			logger.debug("Step 3 - for each sub-seq, get the ddi fragment of the sub-sub-seq");
			Map<String,String> subSequenceListDDIFragment = new HashMap<String,String>(); 
			subSequenceListID.forEach((subSeqkey, subSeqvalue) -> { 
				logger.debug("\t"+subSeqkey);
				String subSequenceFragment = this.getDDIItemByID(subSeqkey);
				subSequenceListDDIFragment.put(subSeqkey, subSequenceFragment);
				Map<String,String> subSubSequenceListID = DDIFunctions.getDDIControlConstructReferenceListFromSequenceFragment(subSequenceFragment);
				// Step 4 - for each sub-sub-seq, get the ddi fragment of the questionItem
				logger.debug("Step 4 - for each sub-sub-seq, get the ddi fragment of the questionItem");
				Map<String,String> subSubSequenceListDDIFragment = new HashMap<String,String>(); 
				subSubSequenceListID.forEach((subSubSeqkey, subSubSeqValue) -> { 
					logger.debug("\t\t"+subSubSeqkey);
					String subSubSequenceFragment = this.getDDIItemByID(subSubSeqkey);
					subSubSequenceListDDIFragment.put(subSubSeqkey, subSubSequenceFragment);
					Map<String,String> questionItemListID = DDIFunctions.getDDIControlConstructReferenceListFromSequenceFragment(subSubSequenceFragment);
					// Step 5 - for each questionItem, get the ddi fragment of the question
					logger.debug("Step 5 - for each questionItem, get the ddi fragment of the question");
					Map<String,String> questionItemListDDIFragment = new HashMap<String,String>(); 
					questionItemListID.forEach((questionItemKey, questionItemValue) -> { 
						logger.debug("\t\t\t"+questionItemKey);
						String questionItemFragment = this.getDDIItemByID(questionItemKey);
						questionItemListDDIFragment.put(questionItemKey, subSequenceFragment);
						Map<String,String> questionListID = DDIFunctions.getDDIQuestionReferenceIDFromFragment(questionItemFragment);
						// Step 6 - for each question, get the ddi fragment of the SourceParameterReference and the TargetParameterReference
						logger.debug("Step 6 - for each question, get the ddi fragment of the SourceParameterReference and the TargetParameterReference");
						Map<String,String> questionListDDIFragment = new HashMap<String,String>();
						questionListID.forEach((questionKey, questionValue) -> { 
							logger.debug("\t\t\t\t"+questionKey);
							String questionFragment = this.getDDIItemByID(questionKey);
							questionListDDIFragment.put(questionKey, questionFragment);
							Map<String,String> sourceParameterReferenceListID = DDIFunctions.getDDISourceParameterReferenceIDFromFragment(questionFragment);
							Map<String,String> sourceParameterReferenceListDDIFragment = new HashMap<String,String>();
							sourceParameterReferenceListID.forEach((sourceParameterReferenceKey, sourceParameterReferenceValue) -> { 
								logger.debug("\t\t\t\t\t"+sourceParameterReferenceKey);
								String sourceParameterReferenceFragment = this.getDDIItemByID(sourceParameterReferenceKey);
								sourceParameterReferenceListDDIFragment.put(sourceParameterReferenceKey, sourceParameterReferenceFragment);
							});
							Map<String,String> targetParameterReferenceListID = DDIFunctions.getDDITargetParameterReferenceIDFromFragment(questionFragment);
							Map<String,String> targetParameterReferenceListDDIFragment = new HashMap<String,String>();
							targetParameterReferenceListID.forEach((targetParameterReferenceKey, targetParameterReferenceValue) -> { 
								logger.debug("\t\t\t\t\t"+targetParameterReferenceKey);
								String targetParameterReferenceFragment = this.getDDIItemByID(targetParameterReferenceKey);
								targetParameterReferenceListDDIFragment.put(targetParameterReferenceKey, targetParameterReferenceFragment);
							});
						});	
					});	
				});			
			});
		});
	return ddi;
	}
	
	
	/**
	 * Main function to perform the JSON model of a questionnaire from id in the repository
	 * 
	 */
	public String getJSONQuestionnaireByID(String id) throws XPathExpressionException, SAXException, IOException, ParserConfigurationException {

		String ddi = getDDIQuestionnaireByID(id);
		PoguesConvertorService serviceConvertor = new PoguesConvertorService();
		String json = serviceConvertor.getJSONQuestionnaireFromDDI(ddi);
		return json;

	}


	/**
	 * Unit function to get an DDI item by id from th repository 
	 * 
	 */
	public String getDDIItemByID(String id) {

		// TODO externalisation of the hostname of the Colectica API
		String url = "https://dvrmesgrepwas01.ad.insee.intra/api/v1/item/fr.insee/";
		url = url + id + "?api_key=ADMINKEY";
		logger.debug(url);
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
	

		    String ddi = client.target(url)
		            .request(MediaType.APPLICATION_XML)
		            .get(String.class);
		    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
		    
		    JSONObject jsonResults = new JSONObject();
			JSONParser parser = new JSONParser();
			try {
				jsonResults = (JSONObject) parser.parse(ddi);
				ddi = (String) jsonResults.get("Item");
			} catch (ParseException e) {
				logger.error("JSON malformed, parsing Exception");
				e.printStackTrace();
			}
	        return ddi;
		    

	}
	
	

}
