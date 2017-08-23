package fr.insee.pogues.api.remote.convertor;

/**
* Pogues Convertor Service to assume the link with the PoguesConvertor API
* 
* @author I6VWID
* 
*
*/
public interface PoguesConvertorService {
	/**
	 *
	 * Call the PoguesConvertor API to product a json from a ddi file
	 *
	 * @param ddi
	 * @return
	 */
	String getJSONQuestionnaireFromDDI(String ddi) throws Exception;

}
