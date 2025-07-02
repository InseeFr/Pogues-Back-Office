package fr.insee.pogues.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.transforms.visualize.ModelTransformer;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.cleaner.ControlCriticityCleaner;
import fr.insee.pogues.utils.model.cleaner.LoopMinMaxCleaner;
import fr.insee.pogues.utils.model.cleaner.ModelCleaner;
import fr.insee.pogues.utils.model.cleaner.TableDimensionCleaner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
public class ModelCleaningService implements ModelTransformer {

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

    @Override
    public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName)
            throws Exception {

        JSONDeserializer jsonDeserializer = new JSONDeserializer();
        Questionnaire questionnaire = jsonDeserializer.deserialize(inputStream);

        cleanModel(questionnaire);

        JSONSerializer jsonSerializer = new JSONSerializer();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(jsonSerializer.serialize(questionnaire).getBytes());
        return outputStream;
    }

}
