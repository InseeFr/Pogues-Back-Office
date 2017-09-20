package fr.insee.pogues.api.remote.metadata.service;

/**
 * Repository Instrument Service to assume the link with the metadata repository
 * 
 * @author I6VWID
 * 
 *
 */
public interface RepositoryQuestionnairesService {

	/**
	 * Main function to perform the DDI model of a questionnaire from id
	 * @return
	 *
	 */
	String getDDIQuestionnaireByID(String id) throws Exception;
	/**
	 * Unit function to get an DDI item by id from th repository
	 *
	 */
	String getDDIItemByID(String id) throws Exception;
	/**
	 * Main function to perform the JSON model of a questionnaire from id in the repository
	 *
	 */
	String getJSONQuestionnaireByID(String id)throws Exception;

}
