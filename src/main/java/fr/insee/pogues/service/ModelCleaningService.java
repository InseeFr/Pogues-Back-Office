package fr.insee.pogues.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.cleaner.ControlCriticityCleaner;
import fr.insee.pogues.utils.model.cleaner.LoopMinMaxCleaner;
import fr.insee.pogues.utils.model.cleaner.ModelCleaner;
import fr.insee.pogues.utils.model.cleaner.TableDimensionCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
public class ModelCleaningService {

    public JsonNode cleanModel(JsonNode jsonNodeQuestionnaire) throws JAXBException, UnsupportedEncodingException, JsonProcessingException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonNodeQuestionnaire);
        cleanModel(questionnaire);
        return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire));
    }

    public void cleanModel(Questionnaire questionnaire) {
        List<ModelCleaner> modelCleaners = List.of(
                new ControlCriticityCleaner(),
                new TableDimensionCleaner(),
                new LoopMinMaxCleaner());
        modelCleaners.forEach(modelCleaner -> modelCleaner.apply(questionnaire));
    }
}
