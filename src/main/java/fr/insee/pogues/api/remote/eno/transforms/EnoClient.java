package fr.insee.pogues.api.remote.eno.transforms;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import fr.insee.pogues.exception.EnoException;

public interface EnoClient {
	/**
	 * 
	 * Call the Eno API to convert ddi 3.2 data in ddi 3.3 
	 * 
	 * @param fileInput
	 * @return String
	 * @throws Exception
	 */
	String getDDI32ToDDI33 (File fileInput) throws Exception;
	
	String getDDIToODT (File fileInput) throws Exception;
	
	String getXMLPoguesToDDI (File fileInput) throws Exception;

	String getDDIToFO(File fileInput) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOLunaticXML(File fileInput) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOLunaticJSON(File fileInput, Map<String, Object> params) throws URISyntaxException, IOException, EnoException;
	
	String getDDITOXForms(File fileInput) throws URISyntaxException, IOException, EnoException;
	
	void getParameters () throws Exception;

}
