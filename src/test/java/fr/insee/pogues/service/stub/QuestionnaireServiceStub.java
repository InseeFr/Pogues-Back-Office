package fr.insee.pogues.service.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.QuestionnaireNotFoundException;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class QuestionnaireServiceStub implements IQuestionnaireService {

    private int getCreateQuestionnaireCalls = 0;
    private final Map<String, JsonNode> questionnaires = new HashMap<>();

    @Override
    public List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception {
        return List.of();
    }

    @Override
    public List<JsonNode> getQuestionnairesStamps() throws Exception {
        return List.of();
    }

    @Override
    public List<JsonNode> getQuestionnairesByOwner(String owner) throws PoguesException {
        if (null == owner || owner.isEmpty()) {
            throw new PoguesException(400, "Bad Request", "");
        }
        return List.of();
    }

    @Override
    public JsonNode getQuestionnaireByID(String id) throws PoguesException {
        JsonNode questionnaire = questionnaires.get(id);
        if (null == questionnaire) {
            throw new QuestionnaireNotFoundException("Not found");
        }
        return questionnaire;
    }

    @Override
    public JsonNode getQuestionnaireByIDWithReferences(String id) throws Exception {
        return null;
    }

    @Override
    public JsonNode getQuestionnaireWithReferences(JsonNode jsonQuestionnaire) throws Exception {
        return null;
    }


    @Override
    public void deleteQuestionnaireByID(String id) throws Exception {
        questionnaires.remove(id);
    }

    @Override
    public void createQuestionnaire(JsonNode questionnaire) throws PoguesException {
        String id = questionnaire.get("id").asText();
        if (null != questionnaires.get(id)) {
            throw new PoguesException(409, "Conflict", "");
        }
        questionnaires.put(id, questionnaire);
        getCreateQuestionnaireCalls++;
    }


    @Override
    public void updateQuestionnaire(String id, JsonNode questionnaire) throws PoguesException {
        if (null == questionnaires.get(id)) {
            throw new PoguesException(404, "Not found", "");
        }
        questionnaires.put(id, questionnaire);
    }
}
