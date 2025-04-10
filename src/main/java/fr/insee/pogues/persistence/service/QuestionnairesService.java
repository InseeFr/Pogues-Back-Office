package fr.insee.pogues.persistence.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by acordier on 05/07/17.
 */
public interface QuestionnairesService {

    List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception;
    
    List<JsonNode> getQuestionnairesStamps() throws Exception;

    /**
     *
     * @param id Should be a known username
     * @return A collection of questionnaire objects mapped to their id
     * @throws Exception
     */
    List<JsonNode> getQuestionnairesByOwner(String id)throws Exception;

    /**
     *
     * @param id Id of requested object
     * @return JSON representation of the questionnaire
     * @throws Exception
     */
    JsonNode getQuestionnaireByID(String id) throws Exception;

    /**
     * A questionnaire can "contain" other questionnaires. These questionnaires appear as references.
     * This method makes it possible to obtain the complete questionnaire, by replacing the references with the complete questionnaires.
     * @param id Id of requested object
     *
     * @return JSON representation of the questionnaire with references
     * @throws Exception
     */
    JsonNode getQuestionnaireByIDWithReferences(String id) throws Exception;

    /**
     * A questionnaire can "contain" other questionnaires. These questionnaires appear as references.
     * This method makes it possible to obtain the complete questionnaire, by replacing the references with the complete questionnaires.
     *
     * @param jsonQuestionnaire JSON representation of a questionnaire
     * @return JSON representation of the questionnaire with its references
     * @throws Exception
     */
    JsonNode getQuestionnaireWithReferences(JsonNode jsonQuestionnaire) throws Exception;

    /**
    *
    * @param id Id of requested object
    * @return JSON Lunatic representation of a questionnaire
    * @throws Exception
    */
    JsonNode getJsonLunaticByID(String id) throws Exception;

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
    void createQuestionnaire(JsonNode questionnaire) throws Exception;
    
    /**
     * Save the JSON Lunatic representation of a questionnaire
     * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
     * @throws Exception
     */
    void createJsonLunatic(JsonNode questionnaireLunatic) throws Exception;

    /**
     * Update a questionnaire object
     * @param questionnaire JSON representation of a questionnaire
     * @param id id of the questionnaire
     * @throws Exception
     */
    void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception;
    
    /**
	 * Update a questionnaire object
	 * @param questionnaireLunatic JSON Lunatic representation of a questionnaire
	 * @param id id of the questionnaire
	 * @throws Exception
	 */
	void updateJsonLunatic(String id, JsonNode questionnaireLunatic) throws Exception;
}
