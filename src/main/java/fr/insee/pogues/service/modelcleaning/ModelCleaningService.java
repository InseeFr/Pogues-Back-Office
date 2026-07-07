package fr.insee.pogues.service.modelcleaning;

import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.exception.PoguesDeserializationException;
import fr.insee.pogues.exception.PoguesSerializationException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.modelcleaning.cleaners.*;
import fr.insee.pogues.transforms.visualize.ModelTransformer;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
@AllArgsConstructor
public class ModelCleaningService implements ModelTransformer {

    private final ModelCleaner nomenclatureRegistryCleaner;

    public JsonNode cleanModel(JsonNode jsonNodeQuestionnaire) throws PoguesDeserializationException, PoguesSerializationException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonNodeQuestionnaire);
        cleanModel(questionnaire);
        return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire));
    }

    public void cleanModel(Questionnaire questionnaire) {
        List<ModelCleaner> modelCleaners = List.of(
                new ControlCriticityCleaner(),
                new TableDimensionCleaner(),
                new ClarificationCleaner(),
                new LoopMinMaxCleaner(),
                new NomenclatureCleaner(),
                nomenclatureRegistryCleaner);
        modelCleaners.forEach(modelCleaner -> modelCleaner.apply(questionnaire));
    }

    @Override
    public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName)
            throws Exception {

        JSONDeserializer jsonDeserializer = new JSONDeserializer();
        Questionnaire questionnaire = jsonDeserializer.deserialize(inputStream);

        cleanModel(questionnaire);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String serialized = new JSONSerializer(true).serialize(questionnaire);
        outputStream.write(serialized.getBytes());
        return outputStream;
    }

}
