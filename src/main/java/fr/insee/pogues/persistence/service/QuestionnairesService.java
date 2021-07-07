package fr.insee.pogues.persistence.service;

import org.json.simple.JSONObject;

import java.util.List;

/**
 * Created by acordier on 05/07/17.
 */
public interface QuestionnairesService {

    List<JSONObject> getQuestionnaireList() throws Exception;
    
    List<JSONObject> getQuestionnairesMetadata(String owner) throws Exception;

    /**
     *
     * @param id Should be a known username
     * @return A collection of questionnaire objects mapped to their id
     * @throws Exception
     */
    List<JSONObject> getQuestionnairesByOwner(String id)throws Exception;

    /**
     *
     * @param id Id of requested object
     * @return
     * @throws Exception
     */
    JSONObject getQuestionnaireByID(String id) throws Exception;
    
    /**
    *
    * @param id Id of requested object
    * @return
    * @throws Exception
    */
   JSONObject getJsonLunaticByID(String id) throws Exception;

    /**
     *
     * @param id Id of the object we want to delete
     * @throws Exception
     */
    void deleteQuestionnaireByID(String id) throws Exception;
    
    /**
    *
    * @param id Id of the object we want to delete
    * @throws Exception
    */
   void deleteJsonLunaticByID(String id) throws Exception;

    /**
     * Create a questionnaire object
     * @param questionnaire
     * @throws Exception
     */
    void createJsonLunatic(JSONObject dataLunatic) throws Exception;
    
    /**
     * Create a questionnaire object
     * @param questionnaire
     * @throws Exception
     */
    void createQuestionnaire(JSONObject questionnaire) throws Exception;


    /**
     * Update a questionnaire object
     * @param questionnaire
     * @throws Exception
     */
    void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception;

	/**
	 * Update a questionnaire object
	 * @param questionnaire
	 * @throws Exception
	 */
	void updateJsonLunatic(String id, JSONObject dataLunatic) throws Exception;
}

