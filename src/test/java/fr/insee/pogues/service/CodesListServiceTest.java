package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
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
    QuestionnaireService questionnaireService;
    @Mock
    VersionService versionService;

    private CodesListService codesListService;

    @BeforeEach
    void initQuestionnaireService(){
        codesListService = new CodesListService(questionnaireService, versionService);
        initMocks(this);
    }

    @Test
    void addCodeListDTOToExistingCodeLists(){
        List<CodeList> existingCodeLists = initFakeCodeLists(8);
        codesListService.addCodeListDTO(existingCodeLists, new CodesListDTO("h-f","Homme-Femme", List.of(
                new CodeDTO("F","Femme",null),
                new CodeDTO("H","Homme",null)
        )));
        assertThat(existingCodeLists).hasSize(9);
        assertEquals("h-f", existingCodeLists.get(8).getId());
    }

    @Test
    void removeCodeListDTOToExistingCodeListsWithId() throws CodesListException {
        List<fr.insee.pogues.model.CodeList> existingCodeLists = initFakeCodeLists(10);
        codesListService.removeCodeListDTO(existingCodeLists, "code-list-4");
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
                new CodesListDTO("test-1","My super CodeList", List.of(
                        new CodeDTO("01","label 1",null),
                        new CodeDTO("02","label 2",null),
                        new CodeDTO("03","label 3",null)
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
                new CodesListDTO("id","label",List.of(
                        new CodeDTO("1","New York",null),
                        new CodeDTO("2","Los Angeles",null),
                        new CodeDTO("3","Chicago",null),
                        new CodeDTO("4","Houston",null),
                        new CodeDTO("5","Phoenix",null),
                        new CodeDTO("6","Philadelphie",null)
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
                new CodesListDTO("id","non-binaire",List.of(
                        new CodeDTO("1","Homme",null),
                        new CodeDTO("2","Femmes",null),
                        new CodeDTO("3","Non-binaire",null),
                        new CodeDTO("4","Agenre ",null),
                        new CodeDTO("5","Cisgenre ",null)
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
                new CodesListDTO("id","h-f",List.of(
                        new CodeDTO("F","Femme",null),
                        new CodeDTO("H","Homme",null)
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
                new CodesListDTO("id","sauce",List.of(
                        new CodeDTO("1","Mayonnaise",null),
                        new CodeDTO("2","Ketchup",null),
                        new CodeDTO("3","Moutarde",null),
                        new CodeDTO("4","Andalouse ",null),
                        new CodeDTO("5","Poivre ",null)
                )));
        assertThat(question.getClarificationQuestion()).isEmpty();
        assertThat(question.getFlowControl()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should be remove because '4' codeValue is removed after updating")
    void shouldRemoveCodeListFilters() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithCodeListFilters = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);
        assertEquals("4",question.getCodeFilters().getFirst().getCodeValue());

        CodesListDTO updatedCodeList = new CodesListDTO("id", "sauce", List.of(
                new CodeDTO("1", "Mayonnaise", null),
                new CodeDTO("2", "Ketchup", null),
                new CodeDTO("3", "Moutarde", null),
                new CodeDTO("3bis", "Poivre", null)
        ));

        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate, updatedCodeList);
        assertThat(question.getCodeFilters()).isEmpty();
    }

    @Test
    @DisplayName("CodeList filters should be conserved because '4' code still exist after updating (adding element and change label value)")
    void shouldNotRemoveCodeListFilters() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithCodeListFilters = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);
        assertEquals("4",question.getCodeFilters().getFirst().getCodeValue());

        CodesListDTO updatedCodeList = new CodesListDTO("id", "sauce", List.of(
                new CodeDTO("1", "Mayonnaise", null),
                new CodeDTO("2", "Ketchup", null),
                new CodeDTO("3", "Moutarde", null),
                new CodeDTO("4", "Andalouse", null),
                new CodeDTO("5", "Poivre", null)
        ));

        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate, updatedCodeList);
        assertThat(question.getCodeFilters()).hasSize(1);
        assertEquals("4",question.getCodeFilters().getFirst().getCodeValue());
    }

    @Test
    @DisplayName("CodeList filters should not be removed after updating the CodeList because it hasn't changed")
    void shouldRemoveCodeListFilters2() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithCodeListFilters = "m8hd7kt3";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithCodeListFilters);
        assertThat(question.getCodeFilters()).hasSize(1);

        CodesListDTO updatedCodeList = new CodesListDTO("id", "sauce", List.of(
                new CodeDTO("1", "Mayonnaise", null),
                new CodeDTO("3", "Ketchup", null),
                new CodeDTO("4", "Moutarde", null)
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
                new CodesListDTO("id","sauce",List.of(
                        new CodeDTO("1","Mayonnaise",null),
                        new CodeDTO("2","Ketchup",null),
                        new CodeDTO("3","Moutarde",null),
                        new CodeDTO("4","Andalouse ",null),
                        new CodeDTO("5","Poivre ",null)
                )));
        assertThat(questionnaire.getVariables().getVariable())
                .anyMatch(variable -> externalVariableID.equals(variable.getId()))
                .anyMatch(variable -> calculatedVariableID.equals(variable.getId()));
    }

    @Test
    @DisplayName("Should add a new CodeList when it does not exist yet")
    void shouldCreateNewCodeList() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String newCodeListId = "new-code-list";
        assertThat(questionnaire.getCodeLists().getCodeList())
                .noneMatch(codeList -> newCodeListId.equals(codeList.getId()));
        CodesListDTO newCodesList = new CodesListDTO(newCodeListId, "new-sauces", List.of(
                new CodeDTO("1", "BBQ", null),
                new CodeDTO("2", "Curry", null)
        ));
        List<String> result = codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, newCodeListId, newCodesList);
        assertThat(questionnaire.getCodeLists().getCodeList())
                .anyMatch(codeList -> newCodeListId.equals(codeList.getId()));
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should get right CodeListDTO from questionnaire")
    void shouldGetRightCodesListsFromQuestionnaire() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        List<ExtendedCodesListDTO> codesLists = codesListService.getCodesListsDTO(questionnaire);
        assertThat(codesLists).hasSize(4);
        assertThat(codesLists.get(0).getRelatedQuestionNames())
                .containsExactly("QUESTION", "TAB", "TAB_SECONDARY", "CHOIXMULTIT");
    }
}
