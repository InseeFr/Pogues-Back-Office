package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.json.JSONFunctions;
import fr.insee.pogues.webservice.rest.PoguesTransforms;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Service
public class PoguesJSONToPoguesJSONDerefImpl implements PoguesJSONToPoguesJSONDeref{

    static final Logger logger = LogManager.getLogger(PoguesJSONToPoguesJSONDerefImpl.class);

    @Autowired
    QuestionnairesService questionnairesService;

    @Override
    public void transform(InputStream input, OutputStream output, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null input");
        }
        if (null == output) {
            throw new NullPointerException("Null output");
        }
        String jsonDeref = transform(input, params, surveyName);
        output.write(jsonDeref.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String transform(InputStream input, Map<String, Object> params, String surveyName) throws Exception {
        if (null == input) {
            throw new NullPointerException("Null input");
        }
        return transform(IOUtils.toString(input, StandardCharsets.UTF_8.name()), params, surveyName);
    }

    @Override
    public String transform(String input, Map<String, Object> params, String surveyName) throws Exception {
        if (!(boolean) params.get("needDeref")) {
            logger.info("No dereferencement needed");
            return input;
        }
        StreamSource json = null;
        JSONSerializer serializer = new JSONSerializer();
        try {
            JSONParser parser = new JSONParser();
            JSONObject questionnaire = (JSONObject) parser.parse(input);
            List<String> references = JSONFunctions.getChildReferencesFromQuestionnaire(questionnaire);
            // We test the existence of the questionnaire in repository
            Questionnaire questionnaireJava = questionnaireToJavaObject(questionnaire);
            if (!references.isEmpty()) {
                references.stream().forEach(ref->{
                    try {
                        insertReference(questionnaireJava, ref);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            logger.info("Sequences inserted");
            return questionnaireJavaToString(questionnaireJava);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Questionnaire questionnaireToJavaObject(JSONObject questionnaire)
            throws JAXBException, PropertyException, IOException {
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

    private String questionnaireJavaToString(Questionnaire quest) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
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
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(baos);
        }
        return "";
    }
    private void insertReference (Questionnaire quest, String ref) throws Exception {
        JSONObject refQuestionnaire = questionnairesService.getQuestionnaireByID(ref);
        Questionnaire refJava = questionnaireToJavaObject(refQuestionnaire);

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
        };
        logger.info("Index to modify", indexOfModification);
        // Suppression of the questionnaire reference
        quest.getChild().remove(indexOfModification);
        // Insertion of the sequences
        for (int i=0; i<refSequences.size();i++) {
            quest.getChild().add(indexOfModification, refJava.getChild().get(refSequences.size()-1-i));
        }
        logger.info("Sequences from {} inserted", ref);

        // 2 - Add variables
        List<VariableType> refVariables = refJava.getVariables().getVariable();
        refVariables.stream().forEach(var->quest.getVariables().getVariable().add(var));
        logger.info("Var from {} inserted", ref);

        // 3 - Add codeslist
        List<CodeList> refCodesList = refJava.getCodeLists().getCodeList();
        refCodesList.stream().forEach(codeList->quest.getCodeLists().getCodeList().add(codeList));
        logger.info("CodeList from {} inserted", ref);

        // 4 - Filters : add flowControl section
        List<FlowControlType> refFlowControl = refJava.getFlowControl();
        refFlowControl.stream().forEach(flowControl->quest.getFlowControl().add(flowControl));
        logger.info("FlowControl from {} inserted", ref);

        // Component group is not updated since it is not used by eno generation

    }
}
