package fr.insee.pogues.transforms.visualize;

import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
    }

}
