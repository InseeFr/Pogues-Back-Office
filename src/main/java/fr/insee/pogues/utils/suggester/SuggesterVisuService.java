package fr.insee.pogues.utils.suggester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.model.CodesList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Class with static method.
 * These method are usefull for visualization with nomenclatures
 */
@Service
public class SuggesterVisuService {

    @Value("${application.api.nomenclatures}")
    private String apiNomenclatures;
    /**
     * Create a JSONObject for queryParam for Visualisation
     * @param nomenclaturesIds
     * @return The expected jsonObject
     * @example_return :{ "id_1": "${apiUrl}/api/nomenclature/${id_1}", "id_2": "${apiUrl}/api/nomenclature/${id_2}"}
     */
    public JsonNode createJsonNomenclaturesForVisu(List<String> nomenclaturesIds){
        ObjectNode finalNomenclatures = JsonNodeFactory.instance.objectNode();
        nomenclaturesIds.forEach(
                id -> {
                    finalNomenclatures.put(id,String.format("%s/api/nomenclature/%s", apiNomenclatures, id));
                }
        );
        return finalNomenclatures;
    }

    /**
     * Retrieve from jsonString representation of Questionnaire (poguesModel), the list of nomenclature's id
     * @param jsonQuestionnairePoguesModel
     * @return List of nomenclatureIds inside questionnaire
     */
    public List<String> getNomenclatureIdsFromQuestionnaire(String jsonQuestionnairePoguesModel) throws JAXBException, JsonProcessingException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonStringtoJsonNode(jsonQuestionnairePoguesModel));
        return getNomenclatureIdsFromQuestionnaire(questionnaire);
    }

    /**
     *  Retrieve from Questionnaire (poguesModel), the list of nomenclature's id
     * @param questionnaire
     * @return List of nomenclatureIds inside questionnaire
     */
    public List<String> getNomenclatureIdsFromQuestionnaire(Questionnaire questionnaire){
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(CodesList::isNomenclatureCodeList)
                .map(CodeList::getId)
                .toList();
    }

}
