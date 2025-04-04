package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.model.dtd.codelists.Code;
import fr.insee.pogues.webservice.model.dtd.codelists.CodesList;
import fr.insee.pogues.webservice.model.dtd.codelists.ExtendedCodesList;
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

import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeLists;
import static fr.insee.pogues.utils.Utils.findQuestionWithId;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionIdWhereCodesListIsUsed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ExtendWith(MockitoExtension.class)
class CodesListServiceTest {

    @Mock
    QuestionnairesService questionnairesService;

    private CodesListService codesListService;

    @BeforeEach
    void initQuestionnaireService(){
        codesListService = new CodesListService(questionnairesService);
        initMocks(this);
    }

    @Test
    void addCodeListDTDToExistingCodeLists(){
        List<CodeList> existingCodeLists = initFakeCodeLists(8);
        codesListService.addCodeListDTD(existingCodeLists, new CodesList("h-f","Homme-Femme", List.of(
                new Code("F","Femme",null),
                new Code("H","Homme",null)
        )));
        assertThat(existingCodeLists).hasSize(9);
        assertEquals("h-f", existingCodeLists.get(8).getId());
    }

    @Test
    void removeCodeListDTDToExistingCodeListsWithId() throws CodesListException {
        List<fr.insee.pogues.model.CodeList> existingCodeLists = initFakeCodeLists(10);
        codesListService.removeCodeListDTD(existingCodeLists, "code-list-4");
        assertEquals(9, existingCodeLists.size());
        assertFalse(existingCodeLists.stream().anyMatch(codeList -> Objects.equals(codeList.getId(), "code-list-4")));
    }

    @Test
    void findQuestionIdWithCodesList() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withCodesLists.json");
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, "m7c68dlm");
        assertTrue(questionIds.contains("m7c61ohr"));
    }

    @Test
    void tryToRemoveExistingCodesList() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withCodesLists.json");
        String codesListToDelete = "m7c68dlm";
        CodesListException exception = assertThrows(
                CodesListException.class,
                () -> codesListService.deleteCodeListOfQuestionnaire(questionnaire, codesListToDelete)
        );
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getRelatedQuestionNames().contains("QUESTION"));
    }

    @Test
    void tryToRemoveExistingCodesListInLoop() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/loop_roudabout.json");
        String codesListToDelete = "m7d5nan9";
        CodesListException exception = assertThrows(
                CodesListException.class,
                () -> codesListService.deleteCodeListOfQuestionnaire(questionnaire, codesListToDelete)
        );
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getRelatedQuestionNames().contains("CODESLISTD"));
    }

    @Test
    void addCodeList() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withoutCodesList.json");
        assertThat(questionnaire.getCodeLists().getCodeList()).isEmpty();
        codesListService.updateOrAddCodeListToQuestionnaire(
                questionnaire,
                "test-1",
                new CodesList("test-1","My super CodeList", List.of(
                        new Code("01","label 1",null),
                        new Code("02","label 2",null),
                        new Code("03","label 3",null)
                ))
        );
        assertThat(questionnaire.getCodeLists().getCodeList()).hasSize(1);
    }

    @Test
    void findQuestionIdWithCodesListInTable() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/table.json");
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, "m7c68dlm");
        assertTrue(questionIds.contains("m7c61ohr"));
        assertTrue(questionIds.contains("m7d6ws56"));
    }


    @Test
    void findQuestionIdWithCodesListInMultipleChoiceQuestion() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/multiple.json");
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, "m7d794ks");
        assertTrue(questionIds.contains("m7d749wl"));
    }

    @Test
    void updateExistingCodeListInMultipleQuestion() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/multiple.json");
        List<CodeList> codeLists = questionnaire.getCodeLists().getCodeList();
        CodeList initialCodeList = codeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), "m7d794ks")).findFirst().get();

        assertThat(codeLists).hasSize(4);
        assertThat(initialCodeList.getCode()).hasSize(3);
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, "m7d794ks",
                new CodesList("id","label",List.of(
                        new Code("1","New York",null),
                        new Code("2","Los Angeles",null),
                        new Code("3","Chicago",null),
                        new Code("4","Houston",null),
                        new Code("5","Phoenix",null),
                        new Code("6","Philadelphie",null)
                )));
        List<CodeList> updatedCodeLists = questionnaire.getCodeLists().getCodeList();
        CodeList codeListUpdated = updatedCodeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), "m7d794ks")).findFirst().get();
        assertThat(updatedCodeLists).hasSize(4);
        assertThat(codeListUpdated.getCode()).hasSize(6);

        String questionnaireasString = PoguesSerializer.questionnaireJavaToString(questionnaire);
        File tmpFile = File.createTempFile("pogues-model-", ".json");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(questionnaireasString);
        writer.close();
        System.out.println(tmpFile.getAbsolutePath());

    }

    @Test
    void updateExistingCodeListInTableQuestion() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/table.json");
        String codesListIdToUpdate = "m7c68dlm";
        List<CodeList> codeLists = questionnaire.getCodeLists().getCodeList();
        CodeList initialCodeList = codeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), codesListIdToUpdate)).findFirst().get();

        assertEquals(3,codeLists.size());
        assertEquals(2,initialCodeList.getCode().size());
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","non-binaire",List.of(
                        new Code("1","Homme",null),
                        new Code("2","Femmes",null),
                        new Code("3","Non-binaire",null),
                        new Code("4","Agenre ",null),
                        new Code("5","Cisgenre ",null)
                )));
        List<CodeList> updatedCodeLists = questionnaire.getCodeLists().getCodeList();
        CodeList codeListUpdated = updatedCodeLists.stream().filter(codeList -> Objects.equals(codeList.getId(), codesListIdToUpdate)).findFirst().get();
        assertEquals(3, updatedCodeLists.size());
        assertEquals(5, codeListUpdated.getCode().size());

        String questionnaireasString = PoguesSerializer.questionnaireJavaToString(questionnaire);
        File tmpFile = File.createTempFile("pogues-model-", ".json");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(questionnaireasString);
        writer.close();
        System.out.println(tmpFile.getAbsolutePath());

    }

    @Test
    @DisplayName("Should conserve column order when updated primary codeList")
    void updateExistingCodeListInTableQuestionComplex() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String questionTableId = "m7d6ws56";
        String codesListIdToUpdate = "m7c68dlm";

        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","h-f",List.of(
                        new Code("F","Femme",null),
                        new Code("H","Homme",null)
                )));
        List<ResponseType> responsesAfter = findQuestionWithId(questionnaire, questionTableId).getResponse();
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
    void shouldRemoveClarificationQuestion() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithClarificationQuestion = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithClarificationQuestion);
        assertThat(question.getClarificationQuestion()).hasSize(1);
        assertThat(question.getFlowControl()).hasSize(1);
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","sauce",List.of(
                        new Code("1","Mayonnaise",null),
                        new Code("2","Ketchup",null),
                        new Code("3","Moutarde",null),
                        new Code("4","Andalouse ",null),
                        new Code("5","Poivre ",null)
                )));
        assertThat(question.getClarificationQuestion()).isEmpty();
        assertThat(question.getFlowControl()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should be removed after updating the CodeList because it has changed")
    void shouldRemoveCodeListFilters() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithCodeListFilters = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);

        CodesList updatedCodeList = new CodesList("id", "sauce", List.of(
                new Code("1", "Mayonnaise", null),
                new Code("2", "Ketchup", null),
                new Code("3", "Moutarde", null),
                new Code("4", "Andalouse", null),
                new Code("5", "Poivre", null)
        ));

        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate, updatedCodeList);
        assertThat(question.getCodeFilters()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should not be removed after updating the CodeList because it hasn't changed")
    void shouldRemoveCodeListFilters2() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithCodeListFilters = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);

        CodesList updatedCodeList = new CodesList("id", "sauce", List.of(
                new Code("1", "Mayonnaise", null),
                new Code("3", "Ketchup", null),
                new Code("4", "Moutarde", null)
        ));

        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate, updatedCodeList);
        assertThat(question.getCodeFilters()).hasSize(1);
    }

    @Test
    @DisplayName("Should not remove CALCULATED and EXTERNAL variables after update code list")
    void shouldNotRemoveCalculatedAndExternalVariables() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String externalVariableID = "m8rd4mu4";
        String calculatedVariableID = "m8rcy1df";
        assertThat(questionnaire.getVariables().getVariable())
                .anyMatch(variable -> externalVariableID.equals(variable.getId()))
                .anyMatch(variable -> calculatedVariableID.equals(variable.getId()));
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","sauce",List.of(
                        new Code("1","Mayonnaise",null),
                        new Code("2","Ketchup",null),
                        new Code("3","Moutarde",null),
                        new Code("4","Andalouse ",null),
                        new Code("5","Poivre ",null)
                )));
        assertThat(questionnaire.getVariables().getVariable())
                .anyMatch(variable -> externalVariableID.equals(variable.getId()))
                .anyMatch(variable -> calculatedVariableID.equals(variable.getId()));
    }

    @Test
    @DisplayName("Should get right CodeListDTD from questionnaire")
    void shouldGetRightCodesListsFromQuestionnaire() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        List<ExtendedCodesList> codesLists = codesListService.getCodesListsDTD(questionnaire);
        assertThat(codesLists).hasSize(4);
        assertThat(codesLists.get(0).getRelatedQuestionNames())
                .containsExactly("QUESTION", "TAB", "TAB_SECONDARY", "CHOIXMULTIT");
    }
}
