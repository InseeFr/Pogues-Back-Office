package fr.insee.pogues.persistence.service;

import org.json.simple.JSONObject;
import fr.insee.pogues.model.VariableType;
import java.util.List;

/**
 * Created by acordier on 05/07/17.
 */
public interface QuestionnairesService {

    List<JSONObject> getQuestionnaireList() throws Exception;
    
    List<JSONObject> getQuestionnairesMetadata(String owner) throws Exception;
    
    List<JSONObject> getQuestionnairesStamps() throws Exception;

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
     * @return JSON representation of the questionnaire
     * @throws Exception
     */
    JSONObject getQuestionnaireByID(String id) throws Exception;
    
    /**
    *
    * @param id Id of requested object
    * @return JSON Lunatic representation of a questionnaire
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
     * Save the JSON representation of a questionnaire
     * @param questionnaire JSON representation of a questionnaire
     * @throws Exception
     */
    void createQuestionnaire(JSONObject questionnaire) throws Exception;
    
    /**
     * Save the JSON Lunatic representation of a questionnaire
     * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
     * @throws Exception
     */
    void createJsonLunatic(JSONObject questionnaireLunatic) throws Exception;

    /**
     * Update a questionnaire object
     * @param questionnaire JSON representation of a questionnaire
     * @param id id of the questionnaire
     * @throws Exception
     */
    void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception;
    
    /**
	 * Update a questionnaire object
	 * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
	 * @param id id of the questionnaire
	 * @throws Exception
	 */
	void updateJsonLunatic(String id, JSONObject questionnaireLunatic) throws Exception;


    /**
     * @param questionnaireId id of the questionnaire
     * @return JSON representation of questionnaire variables
     * @throws Exception
     */
    List<VariableType> getQuestionnaireVariables(String questionnaireId) throws Exception;
}
