package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.conversion.JSONDeserializer;
import fr.insee.pogues.conversion.JSONSerializer;
import fr.insee.pogues.conversion.XMLSerializer;
import fr.insee.pogues.model.ComponentType;
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

import static org.junit.jupiter.api.Assertions.*;


class PoguesJSONToPoguesJSONDerefImplTest {

    /**
     * Tested questionnaire: 'lct78jr8'
     * This questionnaire contains references to the following questionnaires:
     * - 'l4i3m6qa' (with a filter condition in the root questionnaire)
     * - 'l6dnlrka' (simple questionnaire)
     * - 'lct8pcsy' (including loops) */
    @Test
    void testJsonQuestionnaireComposition() throws Exception {
        // Given
        // Load test questionnaire into json objects
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url1 = classLoader.getResource("transforms/PoguesJSONToPoguesJSONDeref/l4i3m6qa.json");
        URL url2 = classLoader.getResource("transforms/PoguesJSONToPoguesJSONDeref/l6dnlrka.json");
        URL url3 = classLoader.getResource("transforms/PoguesJSONToPoguesJSONDeref/lct8pcsy.json");
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
        URL url = classLoader.getResource("transforms/PoguesJSONToPoguesJSONDeref/lct78jr8.json");
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
     * Deserialization issue on the output questionnaire after composition in this case.
     * The reason has not been identified yet.
     */
    @Test
    void testJsonQuestionnaireComposition_issue() throws Exception {
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

}
