package fr.insee.pogues.persistence.service;

import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by acordier on 05/07/17.
 */
public interface QuestionnairesService {
    Map<String, JSONObject> getQuestionnaireList() throws Exception;

    /**
     *
     * @param id Should be a known username
     * @return A collection of questionnaire objects mapped to their id
     * @throws Exception
     */
    Map<String, JSONObject> getQuestionnairesByOwner(String id)throws Exception;

    /**
     *
     * @param id Id of requested object
     * @return
     * @throws Exception
     */
    JSONObject getQuestionnaireByID(String id) throws Exception;

    /**
     *
     * @param id Id of the object we want to delete
     * @throws Exception
     */
    void deleteQuestionnaireByID(String id) throws Exception;

    /**
     * Delete all objects in db, development purpose
     * @throws Exception
     */
    void deleteAllQuestionnaires() throws Exception;

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
    void updateQuestionnaire(JSONObject questionnaire) throws Exception;
}
