package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.model.Questionnaire;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeType;
import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionIdWhereCodesListIsUsed;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodesListTest {

    @Test
    void testGetOnlyCodesWithoutChild() {
        List<CodeType> codes =  List.of(
                initFakeCodeType("H-h","Homme","H"),
                initFakeCodeType("H-f","Femme","H"),
                initFakeCodeType("H","Humain",""),
                initFakeCodeType("M-h","Homme martien","M"),
                initFakeCodeType("M-f","Femme martienne","M"),
                initFakeCodeType("M","Martien martienne",""),
                initFakeCodeType("Z","Seul", "")
        );
        CodeList codeList = new CodeList();
        codeList.getCode().addAll(codes);

        List<CodeType> onlyChild = CodesList.getOnlyCodesWithoutChild(codeList);
        assertEquals(5,onlyChild.size());
        assertTrue(onlyChild.stream().anyMatch(c->c.getLabel().equals("Homme martien")));
        assertTrue(onlyChild.stream().anyMatch(c->c.getLabel().equals("Seul")));
        assertTrue(onlyChild.stream().noneMatch(c->c.getLabel().equals("Humain")));
    }

    @Test
    void findQuestionIdWithCodesList() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/withCodesLists.json");
        List<String> questionIds = getListOfQuestionIdWhereCodesListIsUsed(questionnaire, "m7c68dlm");
        assertTrue(questionIds.contains("m7c61ohr"));
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
}
