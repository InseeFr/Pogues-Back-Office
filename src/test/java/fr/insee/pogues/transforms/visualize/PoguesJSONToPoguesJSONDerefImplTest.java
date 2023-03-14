package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.conversion.XMLSerializer;
import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.ExternalVariableType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for questionnaire composition / de-referencing.
 */
class PoguesJSONToPoguesJSONDerefImplTest {

    /**
     * Tested questionnaire: 'lct78jr8'
     * This questionnaire contains references to the following questionnaires:
     * - 'l4i3m6qa' (with a filter condition in the root questionnaire)
     * - 'l6dnlrka' (simple questionnaire)
     * - 'lct8pcsy' (including loops) */
    @Test
    void dereference_filterAndLoop() throws Exception {
        // Given
        String testRelativePath = "transforms/PoguesJSONToPoguesJSONDeref/filter_and_loop";
        // Load test questionnaire into json objects
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url1 = classLoader.getResource(testRelativePath+"/l4i3m6qa.json");
        URL url2 = classLoader.getResource(testRelativePath+"/l6dnlrka.json");
        URL url3 = classLoader.getResource(testRelativePath+"/lct8pcsy.json");
        assert url1 != null;
        assert url2 != null;
        assert url3 != null;
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonQuestionnaire1 = (JSONObject) jsonParser.parse(Files.readString(Path.of(url1.toURI())));
        JSONObject jsonQuestionnaire2 = (JSONObject) jsonParser.parse(Files.readString(Path.of(url2.toURI())));
        JSONObject jsonQuestionnaire3 = (JSONObject) jsonParser.parse(Files.readString(Path.of(url3.toURI())));
        // Mock questionnaire service
        QuestionnairesService questionnairesService = Mockito.mock(QuestionnairesService.class);
        Mockito.when(questionnairesService.getQuestionnaireByID("l4i3m6qa")).thenReturn(jsonQuestionnaire1);
        Mockito.when(questionnairesService.getQuestionnaireByID("l6dnlrka")).thenReturn(jsonQuestionnaire2);
        Mockito.when(questionnairesService.getQuestionnaireByID("lct8pcsy")).thenReturn(jsonQuestionnaire3);
        // Read tested questionnaire
        URL url = classLoader.getResource(testRelativePath+"/lct78jr8.json");
        assert url != null;
        String testedInput = Files.readString(Path.of(url.toURI()));

        // When
        // Apply de-referencing service
        PoguesJSONToPoguesJSONDerefImpl deref = new PoguesJSONToPoguesJSONDerefImpl(questionnairesService);
        Questionnaire outQuestionnaire = deref.transformAsQuestionnaire(testedInput);

        // Then
        assertNotNull(outQuestionnaire);

        // Test: loops
        assertNotNull(outQuestionnaire.getIterations());
        assertFalse(outQuestionnaire.getIterations().getIteration().isEmpty());
        // Root questionnaire has initially no loops, one of referenced questionnaires has 1 loop.
        assertEquals(1, outQuestionnaire.getIterations().getIteration().size());
        // Check that loop's reference member is in the questionnaire
        String loopMemberRef = outQuestionnaire.getIterations().getIteration().get(0).getMemberReference().get(0);
        assertTrue(outQuestionnaire.getChild().stream()
                .map(ComponentType::getId)
                .anyMatch(loopMemberRef::equals));

        // Test: filters
        FlowControlType flowControlType = outQuestionnaire.getFlowControl().get(0);
        assertNotEquals("l4i3m6qa", flowControlType.getIfTrue().split("-")[0]);
        assertNotEquals("l4i3m6qa", flowControlType.getIfTrue().split("-")[1]);
        // closer look
        String filteredReferenceBeginMember = "l4i3a6ii";
        String filteredReferenceEndMember = "l4i3b1na";
        assertEquals(filteredReferenceBeginMember, flowControlType.getIfTrue().split("-")[0]);
        assertEquals(filteredReferenceEndMember, flowControlType.getIfTrue().split("-")[1]);
    }

    /**
     * Deserialization issue on the output questionnaire after de-referencing in this case.
     * The reason has not been identified yet.
     */
    @Test
    void dereference_issue() throws Exception {
        // Given
        String testRelativePath = "transforms/PoguesJSONToPoguesJSONDeref/translation_issue";
        // Load test questionnaire into json objects
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url1 = classLoader.getResource(testRelativePath+"/referenced1.json");
        URL url2 = classLoader.getResource(testRelativePath+"/referenced2.json");
        assert url1 != null;
        assert url2 != null;
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonQuestionnaire1 = (JSONObject) jsonParser.parse(Files.readString(Path.of(url1.toURI())));
        JSONObject jsonQuestionnaire2 = (JSONObject) jsonParser.parse(Files.readString(Path.of(url2.toURI())));
        // Mock questionnaire service
        QuestionnairesService questionnairesService = Mockito.mock(QuestionnairesService.class);
        Mockito.when(questionnairesService.getQuestionnaireByID("le2v7xet")).thenReturn(jsonQuestionnaire1);
        Mockito.when(questionnairesService.getQuestionnaireByID("le8ffc6k")).thenReturn(jsonQuestionnaire2);
        // Read tested questionnaire
        URL url = classLoader.getResource(testRelativePath+"/reference.json");
        assert url != null;
        String testedInput = Files.readString(Path.of(url.toURI()));

        // When
        // Apply de-referencing service
        PoguesJSONToPoguesJSONDerefImpl deref = new PoguesJSONToPoguesJSONDerefImpl(questionnairesService);
        Questionnaire outQuestionnaire = deref.transformAsQuestionnaire(testedInput);

        // Then
        assertNotNull(outQuestionnaire);
        // (Temp)
        Path testFolder = Path.of("src/test/resources/"+testRelativePath);
        //
        JSONSerializer jsonSerializer = new JSONSerializer();
        String resJson = jsonSerializer.serialize(outQuestionnaire);
        Files.writeString(testFolder.resolve("out/result.json"), resJson);
        //
        XMLSerializer xmlSerializer = new XMLSerializer();
        String resXml = xmlSerializer.serialize(outQuestionnaire);
        Files.writeString(testFolder.resolve("out/result.xml"), resXml);
        //
        PoguesJSONToPoguesXMLImpl poguesJSONToPoguesXML = new PoguesJSONToPoguesXMLImpl();
        String resXmlFromJson = poguesJSONToPoguesXML.transform(new ByteArrayInputStream(resJson.getBytes()), null, null);
        Files.writeString(testFolder.resolve("out/result_from_json.xml"), resXmlFromJson);
        //
        JSONDeserializer jsonDeserializer = new JSONDeserializer();
        Questionnaire questionnaireFromJson = jsonDeserializer.deserialize(
                testFolder.resolve("out/result.json").toAbsolutePath().toString());
        String resXmlFromObjectFromJson = xmlSerializer.serialize(questionnaireFromJson);
        Files.writeString(testFolder.resolve("out/result_from_object_from_json.xml"), resXmlFromObjectFromJson);
    }

    @Test
    void dereference_updatedScopes() throws Exception {
        // Given
        String testRelativePath = "transforms/PoguesJSONToPoguesJSONDeref/iterations_scope";
        // Load test questionnaire into json objects
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL referencedUrl = classLoader.getResource(testRelativePath+"/l4i3m6qa_referenced.json");
        assert referencedUrl != null;
        JSONParser jsonParser = new JSONParser();
        JSONObject referencedJson = (JSONObject) jsonParser.parse(Files.readString(Path.of(referencedUrl.toURI())));
        // Mock questionnaire service
        QuestionnairesService questionnairesService = Mockito.mock(QuestionnairesService.class);
        Mockito.when(questionnairesService.getQuestionnaireByID("l4i3m6qa")).thenReturn(referencedJson);
        // Read tested questionnaire
        URL referenceUrl = classLoader.getResource(testRelativePath+"/leybnsd0_reference.json");
        assert referenceUrl != null;
        String testedInput = Files.readString(Path.of(referenceUrl.toURI()));

        // When
        // Apply de-referencing service
        PoguesJSONToPoguesJSONDerefImpl deref = new PoguesJSONToPoguesJSONDerefImpl(questionnairesService);
        Questionnaire outQuestionnaire = deref.transformAsQuestionnaire(testedInput);

        // Then
        assertNotNull(outQuestionnaire);
        // 3 sequences + 2 sequences in referenced questionnaire + "identquest" end sequence
        assertEquals(6, outQuestionnaire.getChild().size());
        // Referenced questionnaire have an external variable
        Optional<ExternalVariableType> externalVariableInReferenced = outQuestionnaire.getVariables().getVariable()
                .stream()
                .filter(variableType -> variableType instanceof ExternalVariableType)
                .map(variableType -> (ExternalVariableType) variableType)
                .findAny();
        assertTrue(externalVariableInReferenced.isPresent());
        // Referenced questionnaire is in the scope of the loop defined in referencing questionnaire
        // The external variable in referenced questionnaire should have its scope updated
        String iterationId = "leybzt37";
        assertEquals(iterationId, externalVariableInReferenced.get().getScope());
    }

}
