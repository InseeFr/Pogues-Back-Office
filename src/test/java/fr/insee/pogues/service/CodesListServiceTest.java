package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.QuestionType;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.model.dtd.codelists.Code;
import fr.insee.pogues.webservice.model.dtd.codelists.CodesList;
import fr.insee.pogues.webservice.model.dtd.codelists.ExtendedCodesList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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

class CodesListServiceTest {

    @Mock
    QuestionnairesService questionnairesService;

    @InjectMocks
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
        assertEquals(9, existingCodeLists.size());
        assertEquals("h-f", existingCodeLists.get(8).getId());
    }

    @Test
    void removeCodeListDTDToExistingCodeListsWithId() throws CodesListException {
        List<fr.insee.pogues.model.CodeList> existingCodeLists = initFakeCodeLists(10);
        codesListService.removeCodeListDTD(existingCodeLists, "code-list-4");
        assertEquals(9, existingCodeLists.size());
        assertFalse(existingCodeLists.stream().anyMatch(codeList -> codeList.getId() == "code-list-4"));
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
        assertEquals(0, questionnaire.getCodeLists().getCodeList().size());
        codesListService.updateOrAddCodeListToQuestionnaire(
                questionnaire,
                "test-1",
                new CodesList("test-1","My super CodeList", List.of(
                        new Code("01","label 1",null),
                        new Code("02","label 2",null),
                        new Code("03","label 3",null)
                ))
        );
        assertEquals(1, questionnaire.getCodeLists().getCodeList().size());
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

        assertEquals(4,codeLists.size());
        assertEquals(3,initialCodeList.getCode().size());
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
        assertEquals(4, updatedCodeLists.size());
        assertEquals(6, codeListUpdated.getCode().size());

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
    @DisplayName("Should success")
    void updateExistingCodeListInTableQuestionComplex() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7d6wcx1";
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","sauce",List.of(
                        new Code("1","Mayonnaise",null),
                        new Code("2","Ketchup",null),
                        new Code("3","Moutarde",null),
                        new Code("4","Andalouse ",null),
                        new Code("5","Poivre ",null)
                )));
        String questionnaireasString = PoguesSerializer.questionnaireJavaToString(questionnaire);
        File tmpFile = File.createTempFile("pogues-model-", ".json");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(questionnaireasString);
        writer.close();
        System.out.println(tmpFile.getAbsolutePath());
    }

    @Test
    @DisplayName("Should remove clarification Question after update list")
    void shouldRemoveClarificationQuestion() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        String codesListIdToUpdate = "m7c6apvz";
        String questionIdWithClarificationQuestion = "m7c69g2e";
        QuestionType question = findQuestionWithId(questionnaire, questionIdWithClarificationQuestion);
        assertEquals(1, question.getClarificationQuestion().size());
        assertEquals(1, question.getFlowControl().size());
        codesListService.updateOrAddCodeListToQuestionnaire(questionnaire, codesListIdToUpdate,
                new CodesList("id","sauce",List.of(
                        new Code("1","Mayonnaise",null),
                        new Code("2","Ketchup",null),
                        new Code("3","Moutarde",null),
                        new Code("4","Andalouse ",null),
                        new Code("5","Poivre ",null)
                )));
        assertEquals(0, question.getClarificationQuestion().size());
        assertEquals(0, question.getFlowControl().size());

    }

    @Test
    @DisplayName("Should get right CodeListDTD from questionnaire")
    void shouldGetRightCodesListsFromQuestionnaire() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/complexTableWithCodesLists.json");
        List<ExtendedCodesList> codesLists = codesListService.getCodesListsDTD(questionnaire);
        assertEquals(5, codesLists.size());
        assertThat(codesLists.get(0).getRelatedQuestionNames()).containsExactly("QUESTION", "TAB", "TAB_SECONDARY");
    }
}
