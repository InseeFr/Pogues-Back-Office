package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.exception.EnoException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface EnoClient {
	/**
	 * 
	 * Call the Eno API to convert ddi 3.2 data in ddi 3.3 
	 * 
	 * @param inputAsString
	 * @return String
	 * @throws Exception
	 */
	String getDDI32ToDDI33 (String inputAsString) throws Exception;
	
	String getDDIToODT (String inputAsString) throws Exception;
	
	String getXMLPoguesToDDI (String inputAsString) throws Exception;

	String getDDIToFO(String inputAsString) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOLunaticXML(String inputAsString) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOLunaticJSON(String inputAsString, Map<String, Object> params) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOXForms(String inputAsString) throws URISyntaxException, IOException, EnoException;

	String getJSONPoguesToLunaticJson(String inputAsString, Map<String, Object> params) throws URISyntaxException, IOException, EnoException;
	
	void getParameters () throws Exception;

}
