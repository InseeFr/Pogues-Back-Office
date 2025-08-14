package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.model.dto.codeslists.CodeDTO;
import fr.insee.pogues.model.dto.codeslists.CodesListDTO;
import fr.insee.pogues.model.dto.codeslists.ExtendedCodesListDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Objects;

import static fr.insee.pogues.utils.Utils.findQuestionWithId;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class CodesListServiceTest {

    @Mock
    VersionService versionService;

    private CodesListService codesListService;
    private QuestionnaireServiceStub questionnaireService;

    @BeforeEach
    void init(){
        questionnaireService = new QuestionnaireServiceStub();
        codesListService = new CodesListService(questionnaireService, versionService);
        initMocks(this);
    }

    @Test
    @DisplayName("Should fetch questionnaire codes lists")
    void getQuestionnaireCodesLists_success() throws Exception {
        // Given a questionnaire with 1 code list
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's codes lists
        List<ExtendedCodesListDTO> codesLists = codesListService.getQuestionnaireCodesLists("m7c5siu3");

        // Then the code list is fetched
        assertThat(codesLists).hasSize(4);
        assertThat(codesLists.getFirst().getRelatedQuestionNames())
                .containsExactly("QUESTION", "TAB", "TAB_SECONDARY", "CHOIXMULTIT");
    }

    @Test
    void upsertQuestionnaireCodesList_success_created() throws Exception {
        // Given a questionnaire with no code list
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/withoutCodesList.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        assertEquals(0, codesListService.getQuestionnaireCodesLists("lw6534qt").size());

        // When we insert a new code list
        List<String> res = codesListService.upsertQuestionnaireCodesList(
                "lw6534qt",
                "test-1",
                new CodesListDTO("test-1","My super CodeList", List.of(
                        new CodeDTO("01","label 1",null),
                        new CodeDTO("02","label 2",null),
                        new CodeDTO("03","label 3",null)
                ))
        );

        // Then the code list is created and no question is returned
        assertNull(res);
        assertEquals(1, codesListService.getQuestionnaireCodesLists("lw6534qt").size());
    }


    @Test
    void upsertQuestionnaireCodesList_success_updateExistingInMultipleQuestion() throws Exception {
        // Given a questionnaire with a code list in a multiple question
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/multiple.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        List<ExtendedCodesListDTO> initialCodesLists = codesListService.getQuestionnaireCodesLists("m7c5siu3");
        assertEquals(4, initialCodesLists.size());
        ExtendedCodesListDTO initialCodeList = initialCodesLists.stream().filter(codeList -> Objects.equals(codeList.getId(), "m7d794ks")).findFirst().get();
        assertThat(initialCodeList.getCodes()).hasSize(3);

        // When we update the code list
        codesListService.upsertQuestionnaireCodesList("m7c5siu3", "m7d794ks",
                new CodesListDTO("id","label",List.of(
                        new CodeDTO("1","New York",null),
                        new CodeDTO("2","Los Angeles",null),
                        new CodeDTO("3","Chicago",null),
                        new CodeDTO("4","Houston",null),
                        new CodeDTO("5","Phoenix",null),
                        new CodeDTO("6","Philadelphie",null)
                )));

        // Then the code list has been updated
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID("m7c5siu3");
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        List<CodeList> updatedCodeLists = updatedQuestionnaire.getCodeLists().getCodeList();
        CodeList codeListUpdated = updatedCodeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), "m7d794ks")).findFirst().get();
        assertThat(updatedCodeLists).hasSize(4);
        assertThat(codeListUpdated.getCode()).hasSize(6);

        String questionnaireasString = PoguesSerializer.questionnaireJavaToString(updatedQuestionnaire);
        File tmpFile = File.createTempFile("pogues-model-", ".json");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(questionnaireasString);
        writer.close();
        System.out.println(tmpFile.getAbsolutePath());
    }

    @Test
    void upsertQuestionnaireCodesList_success_updateExistingInTableQuestion() throws Exception {
        // Given a questionnaire with a code list in a table question
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/table.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String codeListId = "m7c68dlm";

        List<ExtendedCodesListDTO> initialCodesLists = codesListService.getQuestionnaireCodesLists(questionnaireId);
        assertEquals(3, initialCodesLists.size());
        ExtendedCodesListDTO initialCodeList = initialCodesLists.stream().filter(codeList -> Objects.equals(codeList.getId(), codeListId)).findFirst().get();
        assertThat(initialCodeList.getCodes()).hasSize(2);

        // When we update the code list
        codesListService.upsertQuestionnaireCodesList(questionnaireId, codeListId,
                new CodesListDTO("id","non-binaire",List.of(
                        new CodeDTO("1","Homme",null),
                        new CodeDTO("2","Femmes",null),
                        new CodeDTO("3","Non-binaire",null),
                        new CodeDTO("4","Agenre ",null),
                        new CodeDTO("5","Cisgenre ",null)
                )));

        // Then the code list has been updated
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID(questionnaireId);
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        List<CodeList> updatedCodeLists = updatedQuestionnaire.getCodeLists().getCodeList();
        CodeList codeListUpdated = updatedCodeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), codeListId)).findFirst().get();
        assertThat(updatedCodeLists).hasSize(3);
        assertThat(codeListUpdated.getCode()).hasSize(5);

        String questionnaireasString = PoguesSerializer.questionnaireJavaToString(updatedQuestionnaire);
        File tmpFile = File.createTempFile("pogues-model-", ".json");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(questionnaireasString);
        writer.close();
        System.out.println(tmpFile.getAbsolutePath());

    }

    @Test
    @DisplayName("Should conserve column order when updated primary codeList")
    void upsertQuestionnaireCodesList_success_updateExistingInTableQuestionComplex() throws Exception {
        // Given a questionnaire with a code list in a complex table question
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we update the code list
        codesListService.upsertQuestionnaireCodesList("m7c5siu3", "m7c68dlm",
                new CodesListDTO("id","h-f",List.of(
                        new CodeDTO("F","Femme",null),
                        new CodeDTO("H","Homme",null)
                )));

        // Then the code list has been updated and the column order has not changed
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID("m7c5siu3");
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        List<ResponseType> responsesAfter = findQuestionWithId(updatedQuestionnaire, "m7d6ws56").getResponse();
        assertThat(responsesAfter).hasSize(2*3);
        assertEquals(VisualizationHintEnum.RADIO, responsesAfter.get(0).getDatatype().getVisualizationHint());
        assertEquals(VisualizationHintEnum.RADIO, responsesAfter.get(1).getDatatype().getVisualizationHint());
        assertEquals(DatatypeTypeEnum.NUMERIC, responsesAfter.get(2).getDatatype().getTypeName());
        assertEquals(DatatypeTypeEnum.NUMERIC, responsesAfter.get(3).getDatatype().getTypeName());
        assertEquals(VisualizationHintEnum.DROPDOWN, responsesAfter.get(4).getDatatype().getVisualizationHint());
        assertEquals(VisualizationHintEnum.DROPDOWN, responsesAfter.get(5).getDatatype().getVisualizationHint());
    }

    @Test
    @DisplayName("Should remove clarification Question after update code list")
    void upsertQuestionnaireCodesList_success_removeClarificationQuestion() throws Exception {
        // Given a questionnaire with a code list in a complex table question
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        QuestionType question = findQuestionWithId(mockQuestionnaire, "m8hd7kt3");
        assertThat(question.getClarificationQuestion()).hasSize(1);
        assertThat(question.getFlowControl()).hasSize(1);

        // When we update the code list to remove the clarification modal
        codesListService.upsertQuestionnaireCodesList("m7c5siu3", "m7c6apvz",
                new CodesListDTO("id","sauce",List.of(
                        new CodeDTO("1","Mayonnaise",null),
                        new CodeDTO("2","Ketchup",null),
                        new CodeDTO("3","Moutarde",null),
                        new CodeDTO("4","Andalouse ",null),
                        new CodeDTO("5","Poivre ",null)
                )));

        // Then the clarification question has been removed
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID("m7c5siu3");
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        QuestionType updatedQuestion = findQuestionWithId(updatedQuestionnaire, "m8hd7kt3");
        assertThat(updatedQuestion.getClarificationQuestion()).isEmpty();
        assertThat(updatedQuestion.getFlowControl()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should be remove because '4' codeValue is removed after updating")
    void upsertQuestionnaireCodesList_success_removeCodeListFilters() throws Exception {
        // Given a questionnaire with a code list and a code filter associated to code '4'
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String questionIdWithCodeListFilters = "m8hd7kt3";

        QuestionType question = findQuestionWithId(mockQuestionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);
        assertEquals("4",question.getCodeFilters().getFirst().getCodeValue());

        // When we update the code list to remove the code '4'
        codesListService.upsertQuestionnaireCodesList(questionnaireId, "m7c6apvz",
                new CodesListDTO("id","sauce", List.of(
                        new CodeDTO("1", "Mayonnaise", null),
                        new CodeDTO("2", "Ketchup", null),
                        new CodeDTO("3", "Moutarde", null),
                        new CodeDTO("3bis", "Poivre", null)
                )));

        // Then the question no longer has code filter
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID(questionnaireId);
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        QuestionType updatedQuestion = findQuestionWithId(updatedQuestionnaire, questionIdWithCodeListFilters);
        assertThat(updatedQuestion.getCodeFilters()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should be conserved because '4' code still exist after updating (adding element and change label value)")
    void upsertQuestionnaireCodesList_shouldNotRemoveCodeListFilters() throws Exception {
        // Given a questionnaire with a code list and a code filter associated to code '4'
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String questionIdWithCodeListFilters = "m8hd7kt3";

        QuestionType question = findQuestionWithId(mockQuestionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);
        assertEquals("4",question.getCodeFilters().getFirst().getCodeValue());

        // When we update the code list with new codes (including old '4' one)
        codesListService.upsertQuestionnaireCodesList(questionnaireId, "m7c6apvz", new CodesListDTO("id", "sauce", List.of(
                new CodeDTO("1", "Mayonnaise", null),
                new CodeDTO("2", "Ketchup", null),
                new CodeDTO("3", "Moutarde", null),
                new CodeDTO("4", "Andalouse", null),
                new CodeDTO("5", "Poivre", null)
        )));

        // Then the question still has the code filter associated to code '4'
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID(questionnaireId);
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        QuestionType updatedQuestion = findQuestionWithId(updatedQuestionnaire, questionIdWithCodeListFilters);
        assertThat(updatedQuestion.getCodeFilters()).hasSize(1);
        assertEquals("4",updatedQuestion.getCodeFilters().getFirst().getCodeValue());
    }

    @Test
    @DisplayName("CodeList filters should not be removed after updating the CodeList because it hasn't changed")
    void upsertQuestionnaireCodesList_shouldNotRemoveCodeListFilters2() throws Exception {
        // Given a questionnaire with a code list and filters
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String questionIdWithCodeListFilters = "m8hd7kt3";

        QuestionType question = findQuestionWithId(mockQuestionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);

        // When we update the code list but nothing really change
        codesListService.upsertQuestionnaireCodesList(questionnaireId, "m7c6apvz", new CodesListDTO("id", "sauce", List.of(
                new CodeDTO("1", "Mayonnaise", null),
                new CodeDTO("3", "Ketchup", null),
                new CodeDTO("4", "Moutarde", null)
        )));

        // Then the question still has the code filter
        assertThat(question.getCodeFilters()).hasSize(1);
    }

    @Test
    @DisplayName("Should not remove CALCULATED and EXTERNAL variables after update code list")
    void upsertQuestionnaireCodesList_shouldNotRemoveCalculatedAndExternalVariables() throws Exception {
        // Given a questionnaire with CALCULATED AND EXTERNAL variables
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String externalVariableID = "m8rd4mu4";
        String calculatedVariableID = "m8rcy1df";

        assertThat(mockQuestionnaire.getVariables().getVariable())
                .anyMatch(variable -> externalVariableID.equals(variable.getId()))
                .anyMatch(variable -> calculatedVariableID.equals(variable.getId()));

        // When we update the code list
        codesListService.upsertQuestionnaireCodesList(questionnaireId, "m7c6apvz", new CodesListDTO("id","sauce",List.of(
                new CodeDTO("1","Mayonnaise",null),
                new CodeDTO("2","Ketchup",null),
                new CodeDTO("3","Moutarde",null),
                new CodeDTO("4","Andalouse ",null),
                new CodeDTO("5","Poivre ",null)
        )));

        // Then the CALCULATED AND EXTERNAL variables still exist
        JsonNode updatedQuestionnaireJsonNode = questionnaireService.getQuestionnaireByID(questionnaireId);
        Questionnaire updatedQuestionnaire = PoguesDeserializer.questionnaireToJavaObject(updatedQuestionnaireJsonNode);
        assertThat(updatedQuestionnaire.getVariables().getVariable())
                .anyMatch(variable -> externalVariableID.equals(variable.getId()))
                .anyMatch(variable -> calculatedVariableID.equals(variable.getId()));
    }

    @Test
    void deleteQuestionnaireCodeList_error_usedByQuestions() throws Exception {
        // Given a questionnaire with a code list used in a question
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/withCodesLists.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7c5siu3";
        String codeListId = "m7c68dlm";

        assertEquals(2, codesListService.getQuestionnaireCodesLists(questionnaireId).size());

        // When we delete the code list
        CodesListException exception = assertThrows(
                CodesListException.class,
                () -> codesListService.deleteQuestionnaireCodeList(questionnaireId, codeListId)
        );

        // Then there is a 400 exception, the related question is returned, and no code list has been deleted
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getRelatedQuestionNames().contains("QUESTION"));
        assertEquals(2, codesListService.getQuestionnaireCodesLists(questionnaireId).size());
    }

    @Test
    void deleteQuestionnaireCodeList_error_usedByQuestionInLoop() throws Exception {
        // Given a questionnaire with a code list used in a question in a loop
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loop_roudabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        String questionnaireId = "m7d5k0hy";
        String codeListId = "m7d5nan9";

        assertEquals(1, codesListService.getQuestionnaireCodesLists(questionnaireId).size());

        // When we delete the code list
        CodesListException exception = assertThrows(
                CodesListException.class,
                () -> codesListService.deleteQuestionnaireCodeList(questionnaireId, codeListId)
        );

        // Then there is a 400 exception, the related question is returned, and no code list has been deleted
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getRelatedQuestionNames().contains("CODESLISTD"));
        assertEquals(1, codesListService.getQuestionnaireCodesLists(questionnaireId).size());
    }
}
