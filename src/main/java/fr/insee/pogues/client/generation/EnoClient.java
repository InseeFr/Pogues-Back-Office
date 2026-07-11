package fr.insee.pogues.client.generation;

import fr.insee.pogues.exception.generation.GenerationException;
import fr.insee.pogues.exception.PoguesException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Client with methods to call Eno-WS external web-service.
 */
public interface EnoClient {

	/** Only used as a health-check for the Eno external web-service. */
	void getParameters();

	String getPoguesXmlToDDI(String inputAsString) throws GenerationException, PoguesException;

	String getDDIToODT (String inputAsString) throws GenerationException, PoguesException;

	String getDDIToFO(String inputAsString) throws URISyntaxException, IOException, GenerationException, PoguesException;

	String getDDIToXForms(String inputAsString) throws URISyntaxException, IOException, GenerationException, PoguesException;

	String getPoguesJsonToLunaticJson(String inputAsString, Map<String, Object> params) throws URISyntaxException, IOException, GenerationException, PoguesException;

}
