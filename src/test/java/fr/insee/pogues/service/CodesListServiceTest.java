package fr.insee.pogues.service;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.webservice.model.dtd.codeList.Code;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeLists;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.CodesList.getListOfQuestionIdWhereCodesListIsUsed;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class CodesListServiceTest {

    @Mock
    QuestionnairesService questionnairesService;

    @InjectMocks
    private CodesListService codesListService;
    @InjectMocks
    private VariableService variableService;

    @BeforeEach
    void initQuestionnaireService(){
        codesListService = new CodesListService(questionnairesService, variableService);
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
    void removeCodeListDTDToExistingCodeListsWithId() throws PoguesException {
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
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> codesListService.deleteCodeListOfQuestionnaire(questionnaire, codesListToDelete)
        );
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getDetails().contains("m7c61ohr"));
    }

    @Test
    void tryToRemoveExistingCodesListInLoop() throws Exception {
        Questionnaire questionnaire = loadQuestionnaireFromResources("service/loop_roudabout.json");
        String codesListToDelete = "m7d5nan9";
        PoguesException exception = assertThrows(
                PoguesException.class,
                () -> codesListService.deleteCodeListOfQuestionnaire(questionnaire, codesListToDelete)
        );
        assertEquals(400, exception.getStatus());
        assertTrue(exception.getDetails().contains("m7d5vs4h"));
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
    void updateExistingCodeList() throws Exception {
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
    }

    private Questionnaire loadQuestionnaireFromResources(String uriResources) throws URISyntaxException, IOException, JAXBException {
        URL url = this.getClass().getClassLoader().getResource(uriResources);
        String stringQuestionnaire = Files.readString(Path.of(url.toURI()));
        return PoguesDeserializer.questionnaireToJavaObject(jsonStringtoJsonNode(stringQuestionnaire));
    }
}
