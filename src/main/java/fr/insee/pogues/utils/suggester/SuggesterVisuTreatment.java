package fr.insee.pogues.utils.suggester;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.PoguesDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.bind.JAXBException;
import java.util.List;

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
    public static JSONObject createJsonNomenclaturesForVisu(List<String> nomenclaturesIds, String apiUrl){
        JSONObject finalNomenclatures = new JSONObject();
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
     * @throws ParseException
     */
    public static List<String> getNomenclatureIdsFromQuestionnaire(String jsonQuestionnairePoguesModel) throws ParseException, JAXBException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject((JSONObject) new JSONParser().parse(jsonQuestionnairePoguesModel));
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
