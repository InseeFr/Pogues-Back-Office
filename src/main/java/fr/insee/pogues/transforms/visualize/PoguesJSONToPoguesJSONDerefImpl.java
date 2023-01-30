package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.json.JSONFunctions;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // Parse Pogues json questionnaire
        JSONParser parser = new JSONParser();
        JSONObject jsonQuestionnaire = (JSONObject) parser.parse(input);
        // Get referenced questionnaire identifiers
        // TODO: The "childQuestionnaireRef" in the json should be supported by Pogues-Model
        List<String> references = JSONFunctions.getChildReferencesFromQuestionnaire(jsonQuestionnaire);
        // Deserialize json into questionnaire object
        Questionnaire questionnaire = questionnaireToJavaObject(jsonQuestionnaire);
        //
        deReference(references, questionnaire);
        logger.info("Sequences inserted");
        return questionnaireJavaToString(questionnaire);

    }

    private void deReference(List<String> references, Questionnaire questionnaire) {
        references.forEach(reference -> {
            try {
                JSONObject referencedJsonQuestionnaire = questionnairesService.getQuestionnaireByID(reference);
                if (referencedJsonQuestionnaire == null) {
                    logger.warn(
                            "Null reference behind reference '{}' in questionnaire '{}'.",
                            reference, questionnaire.getId());
                } else {
                    Questionnaire referencedQuestionnaire = questionnaireToJavaObject(referencedJsonQuestionnaire);
                    insertReference(questionnaire, reference, referencedQuestionnaire);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /** This should be moved in Pogues-Model. */
    private Questionnaire questionnaireToJavaObject(JSONObject questionnaire)
            throws JAXBException, IOException {
        StreamSource json;
        if (questionnaire != null) {
            logger.info("Deserializing questionnaire {}", questionnaire.get("id"));
            JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
            unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
            try(InputStream inQuestionnaire = new ByteArrayInputStream(questionnaire.toString().getBytes())){
                json = new StreamSource(inQuestionnaire);
                Questionnaire questionnaireJava = unmarshaller.unmarshal(json, Questionnaire.class).getValue();
                logger.info("Deserializing success");
                return questionnaireJava;
            }
        }
        return null;
    }


    /** This should be moved in Pogues-Model. */
    private String questionnaireJavaToString(Questionnaire quest) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            // Set it to true if you need to include the JSON root element in the JSON output
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            // Set it to true if you need the JSON output to formatted
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            // Marshal the questionnaire object to JSON and put the output in a string
            marshaller.marshal(quest, baos);
            return baos.toString(StandardCharsets.UTF_8);
        } catch (JAXBException | IOException e) {
            logger.error("Unable to serialize Pogues questionnaire '{}'.", quest);
            e.printStackTrace();
            return "";
        }
    }
    private void insertReference(Questionnaire quest, String ref, Questionnaire refJava) {

        // 1- Add sequences
        List<ComponentType> refSequences = refJava.getChild().stream()
                .filter(seq -> !seq.getId().equals("idendquest"))
                .collect(Collectors.toList());
        logger.info("Reference {} retrieved", ref);
        int indexOfModification = 0;
        for (ComponentType seq : quest.getChild()) {
            if (seq.getId().equals(ref)) {
                break;
            }
            indexOfModification++;
        }
        logger.info("Index to modify {}", indexOfModification);
        // Suppression of the questionnaire reference
        quest.getChild().remove(indexOfModification);
        // Insertion of the sequences
        for (int i=0; i<refSequences.size();i++) {
            quest.getChild().add(indexOfModification, refJava.getChild().get(refSequences.size()-1-i));
        }
        logger.info("Sequences from {} inserted", ref);

        // 2 - Add variables
        List<VariableType> refVariables = refJava.getVariables().getVariable();
        refVariables.forEach(variable -> quest.getVariables().getVariable().add(variable));
        logger.info("Var from {} inserted", ref);

        // 3 - Add codeslist
        List<CodeList> refCodesList = refJava.getCodeLists().getCodeList();
        refCodesList.forEach(codeList -> quest.getCodeLists().getCodeList().add(codeList));
        logger.info("CodeList from {} inserted", ref);

        // 4 - Filters : add flowControl section
        List<FlowControlType> refFlowControl = refJava.getFlowControl();
        refFlowControl.forEach(flowControl -> quest.getFlowControl().add(flowControl));
        logger.info("FlowControl from {} inserted", ref);

        // Component group is not updated since it is not used by eno generation
    }
}
