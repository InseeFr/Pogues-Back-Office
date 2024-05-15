package fr.insee.pogues.utils.suggester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.PoguesDeserializer;

import javax.xml.bind.JAXBException;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Class with static method.
 * These method are usefull for visualization with nomenclatures
 */
public class SuggesterVisuTreatment {
    private SuggesterVisuTreatment() {

    }

    /**
     * Create a JSONObject for queryParam for Visualisation
     * @param nomenclaturesIds
     * @param apiUrl
     * @return The expected jsonObject
     * @example_return :{ "id_1": "${apiUrl}/api/nomenclature/${id_1}", "id_2": "${apiUrl}/api/nomenclature/${id_2}"}
     */
    public static JsonNode createJsonNomenclaturesForVisu(List<String> nomenclaturesIds, String apiUrl){
        ObjectNode finalNomenclatures = JsonNodeFactory.instance.objectNode();
        nomenclaturesIds.forEach(
                id -> {
                    finalNomenclatures.put(id,String.format("%s/api/nomenclature/%s", apiUrl, id));
                }
        );
        return finalNomenclatures;
    }

    /**
     * Retrieve from jsonString representation of Questionnaire (poguesModel), the list of nomenclature's id
     * @param jsonQuestionnairePoguesModel
     * @return List of nomenclatureIds inside questionnaire
     */
    public static List<String> getNomenclatureIdsFromQuestionnaire(String jsonQuestionnairePoguesModel) throws JAXBException, JsonProcessingException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonStringtoJsonNode(jsonQuestionnairePoguesModel));
        return getNomenclatureIdsFromQuestionnaire(questionnaire);
    }

    /**
     *  Retrieve from Questionnaire (poguesModel), the list of nomenclature's id
     * @param questionnaire
     * @return List of nomenclatureIds inside questionnaire
     */
    public static List<String> getNomenclatureIdsFromQuestionnaire(Questionnaire questionnaire){
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(codeList -> codeList.getSuggesterParameters() != null)
                .map(codeList -> codeList.getId())
                .toList();
    }
}
