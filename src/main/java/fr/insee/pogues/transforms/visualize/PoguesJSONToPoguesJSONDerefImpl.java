package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.exception.IllegalFlowControlException;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PoguesJSONToPoguesJSONDerefImpl implements PoguesJSONToPoguesJSONDeref{

    static final Logger logger = LogManager.getLogger(PoguesJSONToPoguesJSONDerefImpl.class);

    /** Name of the artificial end sequence added by the front (to manage some GoTo cases). */
    public static final String FAKE_LAST_ELEMENT_ID = "idendquest";

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
        return questionnaireJavaToString(questionnaire);
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
        Questionnaire questionnaire = questionnaireToJavaObject(jsonQuestionnaire);
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
                Questionnaire referencedQuestionnaire = questionnaireToJavaObject(referencedJsonQuestionnaire);
                insertReference(questionnaire, reference, referencedQuestionnaire);
            }
        }
    }

    /** This should be moved in Pogues-Model. */
    private Questionnaire questionnaireToJavaObject(JSONObject jsonQuestionnaire)
            throws JAXBException, IOException {
        StreamSource json;
        logger.info("Deserializing questionnaire {}", jsonQuestionnaire.get("id"));
        JAXBContext context = JAXBContext.newInstance(Questionnaire.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
        unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
        try (InputStream inQuestionnaire = new ByteArrayInputStream(jsonQuestionnaire.toString().getBytes())) {
            json = new StreamSource(inQuestionnaire);
            Questionnaire questionnaire = unmarshaller.unmarshal(json, Questionnaire.class).getValue();
            logger.info("Deserializing success");
            return questionnaire;
        }
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

    /**
     * Replace referenced questionnaire by its component. Update elements that are impacted.
     * @param questionnaire Referencing questionnaire.
     * @param reference Identifier of the referenced questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire object.
     * @throws IllegalFlowControlException if the 'IfTrue' property of a FlowControl object is invalid.
     */
    private void insertReference(Questionnaire questionnaire, String reference, Questionnaire referencedQuestionnaire)
            throws IllegalFlowControlException {
        // Coherence check
        if (! reference.equals(referencedQuestionnaire.getId())) {
            logger.warn("Reference '{}' found in questionnaire '{}' mismatch referenced questionnaire's id '{}'",
                    reference, questionnaire.getId(), referencedQuestionnaire.getId());
        }

        // Add sequences
        List<ComponentType> refSequences = referencedQuestionnaire.getChild().stream()
                .filter(seq -> !seq.getId().equals(FAKE_LAST_ELEMENT_ID))
                .collect(Collectors.toList());
        logger.info("Reference {} retrieved", reference);
        int indexOfModification = 0;
        for (ComponentType seq : questionnaire.getChild()) {
            if (seq.getId().equals(reference)) {
                break;
            }
            indexOfModification++;
        }
        logger.info("Index to modify {}", indexOfModification);
        // Suppression of the questionnaire reference
        questionnaire.getChild().remove(indexOfModification);
        // Insertion of the sequences
        for (int i=0; i<refSequences.size();i++) {
            questionnaire.getChild().add(indexOfModification, referencedQuestionnaire.getChild().get(refSequences.size()-1-i));
        }
        logger.info("Sequences from {} inserted", reference);

        // Add variables
        List<VariableType> refVariables = referencedQuestionnaire.getVariables().getVariable();
        refVariables.forEach(variable -> questionnaire.getVariables().getVariable().add(variable));
        logger.info("Variables from {} inserted", reference);

        // Add code lists
        List<CodeList> refCodesList = referencedQuestionnaire.getCodeLists().getCodeList();
        refCodesList.forEach(codeList -> questionnaire.getCodeLists().getCodeList().add(codeList));
        logger.info("CodeList from {} inserted", reference);

        // Filters defined on referenced questionnaire
        for (FlowControlType flowControlType : questionnaire.getFlowControl()) {
            updateFlowControlBounds(reference, referencedQuestionnaire, flowControlType);
        }
        logger.info("FlowControl member references updated from {}", reference);

        // Filters : add flowControl section
        List<FlowControlType> refFlowControl = referencedQuestionnaire.getFlowControl();
        refFlowControl.forEach(flowControl -> questionnaire.getFlowControl().add(flowControl));
        logger.info("FlowControl from {} inserted", reference);

        // Loops: add iterations (loops)
        Questionnaire.Iterations refIterations = referencedQuestionnaire.getIterations();
        if (refIterations != null) {
            questionnaire.setIterations(new Questionnaire.Iterations());
            List<IterationType> iterationList = questionnaire.getIterations().getIteration();
            iterationList.addAll(refIterations.getIteration());
            logger.info("Iterations from {} inserted", reference);
        } else {
            logger.info("No iterations in referenced questionnaire {}", reference);
        }

        // Component group is not updated since it is not used by eno generation
    }

    /** Replace filter bounds that reference a questionnaire by its first or last sequence.
     * @param reference Identifier of the referenced questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     * @param flowControlType The FlowControl object to be updated.
     * @throws IllegalFlowControlException If the FlowControl 'IfTrue' property doesn't match the format "id-id".
     */
    private static void updateFlowControlBounds(String reference, Questionnaire referencedQuestionnaire, FlowControlType flowControlType)
            throws IllegalFlowControlException {
        if (flowControlType.getIfTrue() == null) {
            throw new IllegalFlowControlException(String.format(
                    "'IfTrue' property is null in FlowControl '%s'",
                    flowControlType.getId()));
        }
        // The 'IfTrue' property defines begin/end member references (separated with '-') of the filter
        String[] flowControlBounds = flowControlType.getIfTrue().split("-");
        if (flowControlBounds.length != 2) {
            throw new IllegalFlowControlException(String.format(
                    "'IfTrue' value '%s' is not compliant with Pogues-Model specification in FlowControl '%s'",
                    flowControlType.getIfTrue(), flowControlType.getId()));
        }
        // Replace questionnaire reference by its first/last sequence
        String beginMember = flowControlBounds[0];
        String endMember = flowControlBounds[1];
        if (beginMember.equals(reference)) {
            beginMember = referencedQuestionnaire.getChild().get(0).getId();
        }
        if (endMember.equals(reference)) {
            List<ComponentType> referenceSequences = referencedQuestionnaire.getChild().stream()
                    .filter(componentType -> !FAKE_LAST_ELEMENT_ID.equals(componentType.getId()))
                    .collect(Collectors.toList());
            endMember = referenceSequences.get(referenceSequences.size() - 1).getId();
        }
        flowControlType.setIfTrue(beginMember+"-"+endMember);
    }

}
