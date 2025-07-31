package fr.insee.pogues.service.stub;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class StubQuestionnaireService implements IQuestionnaireService {

    private int getCreateQuestionnaireCalls = 0;
    private final Map<String, JsonNode> fakeQuestionnaires = new HashMap<>();

    @Override
    public List<JsonNode> getQuestionnairesMetadata(String owner) throws Exception {
        return List.of();
    }

    @Override
    public List<JsonNode> getQuestionnairesStamps() throws Exception {
        return List.of();
    }

    @Override
    public List<JsonNode> getQuestionnairesByOwner(String id) throws Exception {
        return List.of();
    }

    @Override
    public JsonNode getQuestionnaireByID(String id) throws Exception {
        return null;
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
        /* Does nothing for now (stub) */
    }

    @Override
    public void createQuestionnaire(JsonNode questionnaire) throws Exception {
        getCreateQuestionnaireCalls++;
    }


    @Override
    public void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception {
        /* Does nothing for now (stub) */
    }
}
