package fr.insee.pogues.service;


import fr.insee.pogues.exception.PoguesDeserializationException;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Class with static method.
 * These method are usefull for visualization with nomenclatures
 */
@Service
public class SuggesterVisuService {

    @Value("${application.survey-registry.host}")
    private String surveyRegistryApi;

    private final IQuestionnaireService questionnaireService;

    public SuggesterVisuService(IQuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    /**
     * Retrieve from questionnaire id, the list of nomenclature's id and url used by the questionnaire.
     * @param questionnaireId
     * @return List of nomenclature's id and url
     */
    public List<NomenclatureUrlDTO> computeNomenclaturesUrls(String questionnaireId) throws Exception {
        Questionnaire questionnaire = questionnaireService.getQuestionnaireModelByIDWithReferences(questionnaireId);
        List<String> ids = getNomenclaturesIdsFromQuestionnaire(questionnaire);
        return computeNomenclaturesUrls(ids);
    }

    /**
     * Create from nomenclature ids, the list of nomenclature's id and url.
     * @param nomenclatureIds
     * @return List of nomenclatures id and url
     */
    public List<NomenclatureUrlDTO> computeNomenclaturesUrls(List<String> nomenclatureIds) {
        return nomenclatureIds.stream()
                .map(id -> new NomenclatureUrlDTO(id, String.format("%s/codes-lists/%s", surveyRegistryApi, id)))
                .toList();
    }

    /**
     * Create a JSONObject for queryParam for Visualisation
     * @param nomenclaturesIds
     * @return The expected jsonObject
     * @example_return :{ "id_1": "${surveyRegistryApi}/codes-lists/${id_1}", "id_2": "${surveyRegistryApi}/codes-lists/${id_2}"}
     */
    public JsonNode createJsonNomenclaturesForVisu(List<String> nomenclaturesIds) {
        ObjectNode finalNomenclatures = JsonNodeFactory.instance.objectNode();
        computeNomenclaturesUrls(nomenclaturesIds)
                .forEach(nomenclature -> finalNomenclatures.put(nomenclature.id(), nomenclature.url()));
        return finalNomenclatures;
    }

    /**
     * Retrieve from jsonString representation of Questionnaire (poguesModel), the list of nomenclature's id
     * @param jsonQuestionnairePoguesModel
     * @return List of nomenclatureIds inside questionnaire
     */
    public List<String> getNomenclaturesIdsFromQuestionnaire(String jsonQuestionnairePoguesModel) throws PoguesDeserializationException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonStringtoJsonNode(jsonQuestionnairePoguesModel));
        return getNomenclaturesIdsFromQuestionnaire(questionnaire);
    }

    /**
     *  Retrieve from Questionnaire (poguesModel), the list of nomenclature's id
     * @param questionnaire
     * @return List of nomenclatureIds inside questionnaire
     */
    public List<String> getNomenclaturesIdsFromQuestionnaire(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .map(CodeList::getId)
                .toList();
    }

}
