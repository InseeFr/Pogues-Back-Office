package fr.insee.pogues.api.remote.eno.transforms;

import fr.insee.pogues.exception.EnoException;
import fr.insee.pogues.webservice.rest.PoguesException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Client with methods to call Eno-WS external web-service.
 */
public interface EnoClient {

	/** Only used as a health-check for the Eno external web-service. */
	void getParameters();

	String getPoguesXmlToDDI(String inputAsString) throws EnoException, PoguesException;

	String getDDIToODT (String inputAsString) throws EnoException, PoguesException;

	String getDDIToFO(String inputAsString) throws URISyntaxException, IOException, EnoException;

	String getDDIToXForms(String inputAsString) throws URISyntaxException, IOException, EnoException;

	/** @deprecated Use Pogues to Lunatic method instead. */
	@Deprecated(since = "4.9.2")
	String getDDIToLunaticJSON(String inputAsString, Map<String, Object> params) throws URISyntaxException, IOException, EnoException;

	String getPoguesJsonToLunaticJson(String inputAsString, Map<String, Object> params) throws URISyntaxException, IOException, EnoException;

}
