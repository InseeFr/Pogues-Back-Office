package fr.insee.pogues.transforms.visualize;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.suggester.SuggesterVisuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import static fr.insee.pogues.utils.IOStreamsUtils.*;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
public class PoguesJSONToPoguesJSONDerefImpl implements PoguesJSONToPoguesJSONDeref{

    @Autowired
    QuestionnairesService questionnairesService;

    @Autowired
    SuggesterVisuService suggesterVisuService;

    public PoguesJSONToPoguesJSONDerefImpl() {}

    public PoguesJSONToPoguesJSONDerefImpl(QuestionnairesService questionnairesService) {
        this.questionnairesService = questionnairesService;
    }

    @Override
    public ByteArrayOutputStream transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null input stream in 'Pogues to Pogues dereferenced' transform method.");
        }
        // TODO: This parameter could be replaced by logical check in back-office
        // (when Pogues-Model supports "childQuestionnaireRef")
        if (!(boolean) params.get("needDeref")) {
            log.info("No de-referencing needed");
            return input2Output(input);
        }
        Questionnaire questionnaire = transformAsQuestionnaire(inputStream2String(input));
        // Update nomenclatureIds with ids from references
        params.put("nomenclatureIds", suggesterVisuService.getNomenclatureIdsFromQuestionnaire(questionnaire));
        String questionnaireAsString = PoguesSerializer.questionnaireJavaToString(questionnaire);
        return string2BOAS(questionnaireAsString);
    }

    public Questionnaire transformAsQuestionnaire(String input) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null string cannot be converted to a questionnaire.");
        }
        JsonNode jsonQuestionnaire = jsonStringtoJsonNode(input);
        JsonNode questionnaireWithRef = questionnairesService.getQuestionnaireWithReferences(jsonQuestionnaire);
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireWithRef);
    }

}
