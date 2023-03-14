package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.transforms.visualize.composition.QuestionnaireComposition;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class PoguesJSONToPoguesJSONDerefImpl implements PoguesJSONToPoguesJSONDeref{

    static final Logger logger = LogManager.getLogger(PoguesJSONToPoguesJSONDerefImpl.class);

    private static final String NULL_INPUT_MESSAGE = "Null input";
    private static final String NULL_OUTPUT_MESSAGE = "Null output";

    @Autowired
    QuestionnairesService questionnairesService;

    public PoguesJSONToPoguesJSONDerefImpl() {}

    public PoguesJSONToPoguesJSONDerefImpl(QuestionnairesService questionnairesService) {
        this.questionnairesService = questionnairesService;
    }

    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException(NULL_INPUT_MESSAGE);
        }
        if (null == output) {
            throw new NullPointerException(NULL_OUTPUT_MESSAGE);
        }
        String jsonDeref = transform(input, params, surveyName);
        output.write(jsonDeref.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException(NULL_INPUT_MESSAGE);
        }
        return transform(IOUtils.toString(input, StandardCharsets.UTF_8), params, surveyName);
    }

    @Override
    public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException(NULL_INPUT_MESSAGE);
        }
        // TODO: This parameter could be replaced by logical check in back-office
        // (when Pogues-Model supports "childQuestionnaireRef")
        if (!(boolean) params.get("needDeref")) {
            logger.info("No de-referencing needed");
            return input;
        }
        Questionnaire questionnaire = transformAsQuestionnaire(input);
        return PoguesSerializer.questionnaireJavaToString(questionnaire);
    }

    public Questionnaire transformAsQuestionnaire(String input) throws Exception {
        if (null == input) {
            throw new NullPointerException(NULL_INPUT_MESSAGE);
        }
        // Parse Pogues json questionnaire
        JSONParser parser = new JSONParser();
        JSONObject jsonQuestionnaire = (JSONObject) parser.parse(input);
        // Get referenced questionnaire identifiers
        // TODO: The "childQuestionnaireRef" in the json should be supported by Pogues-Model
        List<String> references = JSONFunctions.getChildReferencesFromQuestionnaire(jsonQuestionnaire);
        // Deserialize json into questionnaire object
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonQuestionnaire);
        //
        deReference(references, questionnaire);
        logger.info("Sequences inserted");
        //
        return questionnaire;
    }

    private void deReference(List<String> references, Questionnaire questionnaire) throws Exception {
        for (String reference : references) {
            JSONObject referencedJsonQuestionnaire = questionnairesService.getQuestionnaireByID(reference);
            if (referencedJsonQuestionnaire == null) {
                logger.warn(
                        "Null reference behind reference '{}' in questionnaire '{}'.",
                        reference, questionnaire.getId());
            } else {
                Questionnaire referencedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(referencedJsonQuestionnaire);
                // Coherence check
                if (! reference.equals(referencedQuestionnaire.getId())) {
                    logger.warn("Reference '{}' found in questionnaire '{}' mismatch referenced questionnaire's id '{}'",
                            reference, questionnaire.getId(), referencedQuestionnaire.getId());
                }
                //
                QuestionnaireComposition.insertReference(questionnaire, referencedQuestionnaire);
            }
        }
    }

}
