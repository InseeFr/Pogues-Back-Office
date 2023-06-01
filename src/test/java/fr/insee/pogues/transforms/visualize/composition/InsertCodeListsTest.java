package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertCodeListsTest {

    private final Questionnaire questionnaire = new Questionnaire();
    private final Questionnaire referenced1 = new Questionnaire();
    private final Questionnaire referenced2 = new Questionnaire();

    @BeforeEach
    public void createQuestionnaires() {
        QuestionnaireCompositionTest.questionnairesContent(questionnaire, referenced1, referenced2);
    }

    @Test
    void insertReference_codeLists() {
        //
        CodeList codeList = new CodeList();
        codeList.setId("codes11");
        referenced1.setCodeLists(new CodeLists());
        referenced1.getCodeLists().getCodeList().add(codeList);
        //
        assertNull(questionnaire.getCodeLists());
        //
        InsertCodeLists insertCodeLists = new InsertCodeLists();
        insertCodeLists.apply(questionnaire, referenced1);
        //
        assertNotNull(questionnaire.getCodeLists());
        assertFalse(questionnaire.getCodeLists().getCodeList().isEmpty());
        assertEquals("codes11", questionnaire.getCodeLists().getCodeList().get(0).getId());
    }

    @Test
    void insertCodeList_differentLabels() {
        //
        CodeList codeList = new CodeList();
        codeList.setId("codes1");
        codeList.setLabel("CODE_LIST_A");
        questionnaire.setCodeLists(new CodeLists());
        questionnaire.getCodeLists().getCodeList().add(codeList);
        //
        CodeList codeListRef = new CodeList();
        codeListRef.setId("codes11");
        codeListRef.setLabel("CODE_LIST_B");
        referenced1.setCodeLists(new CodeLists());
        referenced1.getCodeLists().getCodeList().add(codeListRef);
        //
        InsertCodeLists insertCodeLists = new InsertCodeLists();
        insertCodeLists.apply(questionnaire, referenced1);
        //
        assertNotNull(questionnaire.getCodeLists());
        assertFalse(questionnaire.getCodeLists().getCodeList().isEmpty());
        assertEquals(2, questionnaire.getCodeLists().getCodeList().size());
    }

    @Test
    void insertCodeList_sameLabel() {
        //
        CodeList codeList = new CodeList();
        codeList.setId("codes1");
        codeList.setLabel("CODE_LIST_A");
        questionnaire.setCodeLists(new CodeLists());
        questionnaire.getCodeLists().getCodeList().add(codeList);
        //
        CodeList codeListRef = new CodeList();
        codeListRef.setId("codes11");
        codeListRef.setLabel("CODE_LIST_A");
        referenced1.setCodeLists(new CodeLists());
        referenced1.getCodeLists().getCodeList().add(codeListRef);
        //
        InsertCodeLists insertCodeLists = new InsertCodeLists();
        insertCodeLists.apply(questionnaire, referenced1);
        //
        assertNotNull(questionnaire.getCodeLists());
        assertFalse(questionnaire.getCodeLists().getCodeList().isEmpty());
        assertEquals(1, questionnaire.getCodeLists().getCodeList().size());
        assertEquals("codes1", questionnaire.getCodeLists().getCodeList().get(0).getId());
    }

}
