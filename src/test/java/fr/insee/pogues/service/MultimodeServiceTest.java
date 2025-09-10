package fr.insee.pogues.service;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.controller.error.ErrorCode;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.QuestionnaireFormulaLanguageNotVTLException;
import fr.insee.pogues.exception.QuestionnaireNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.service.stub.QuestionnaireServiceStub;
import fr.insee.pogues.utils.PoguesSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static fr.insee.pogues.utils.Utils.loadQuestionnaireFromResources;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MultimodeServiceTest {

    @Mock
    VersionService versionService;

    /** Multimode defined in the multimode.json questionnaire */
    private Multimode qMultimode = new Multimode();

    private MultimodeService multimodeService;
    private QuestionnaireServiceStub questionnaireService;

    @BeforeEach
    void init() {
        questionnaireService = new QuestionnaireServiceStub();
        multimodeService = new MultimodeService(questionnaireService, versionService);

        qMultimode = new Multimode();
        Rule ruleQ = new Rule();
        ruleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleQ.setValue("nvl(HABITEZ_VOUS_ICI, true)");
        ruleQ.setType(ValueTypeEnum.VTL);
        Rules rulesQ = new Rules();
        rulesQ.getRules().add(ruleQ);
        qMultimode.setQuestionnaire(rulesQ);
        Rule ruleL1 = new Rule();
        ruleL1.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL1.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL1.setType(ValueTypeEnum.VTL);
        Rule ruleL2 = new Rule();
        ruleL2.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleL2.setValue("nvl(fzqfqzgjh, false)");
        ruleL2.setType(ValueTypeEnum.VTL);
        Rule ruleL3 = new Rule();
        ruleL3.setName(MultimodeRuleNameEnum.IS_SPLIT);
        ruleL3.setValue("nvl(PRENOM_HABITE_PLUS_LA, false)");
        ruleL3.setType(ValueTypeEnum.VTL);
        Rules rulesL = new Rules();
        rulesL.getRules().addAll(List.of(ruleL1, ruleL2, ruleL3));
        qMultimode.setLeaf(rulesL);
    }

    @Test
    @DisplayName("Should fetch questionnaire multimode")
    void getQuestionnaireMultimode_success() throws Exception {
        // Given a questionnaire in VTL with a roundabout and a multimode
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/multimode.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's multimode
        Multimode res = multimodeService.getQuestionnaireMultimode("multimode-id");

        // Then the multimode is fetched
        assertThat(res).usingRecursiveComparison().isEqualTo(qMultimode);
    }

    @Test
    @DisplayName("Should return null when there is no multimodes")
    void getQuestionnaireMultimode_success_null() throws Exception {
        // Given a questionnaire in VTL with a roundabout and no multimode
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's multimode
        Multimode res = multimodeService.getQuestionnaireMultimode("m7d5k0hy");

        // Then the result is null
        assertNull(res);
    }

    @Test
    @DisplayName("Should throw an exception when there is no questionnaires related to the id")
    void getQuestionnaireMultimode_error_notFound() {
        // Given a questionnaire that does not exist

        // When we get the questionnaire's multimode
        PoguesException exception = assertThrows(
                QuestionnaireNotFoundException.class,
                () -> multimodeService.getQuestionnaireMultimode("q-id"));

        // Then an exception is thrown because this questionnaire does not exist
        assertEquals(404, exception.getStatus());
        assertEquals(ErrorCode.QUESTIONNAIRE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should throw an exception when not in VTL")
    void getQuestionnaireMultimode_error_notVTL() throws Exception {
        // Given a questionnaire not in VTL with a roundabout
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundaboutXPath.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);

        // When we get the questionnaire's multimode
        PoguesException exception = assertThrows(
                QuestionnaireFormulaLanguageNotVTLException.class,
                () -> multimodeService.getQuestionnaireMultimode("m7d5k0hy"));

        // Then an exception is thrown because this feature is not available
        assertEquals(422, exception.getStatus());
        assertEquals(ErrorCode.QUESTIONNAIRE_FORMULA_LANGUAGE_NOT_VTL, exception.getErrorCode());
    }

    @Test
    @DisplayName("Should update questionnaire multimode")
    void upsertQuestionnaireMultimode_success_updated() throws Exception {
        // Given a questionnaire in VTL with a roundabout and a multimode
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/multimode.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        assertThat(multimodeService.getQuestionnaireMultimode("multimode-id")).usingRecursiveComparison().isEqualTo(qMultimode);

        // When we update the questionnaire's multimode
        Multimode newMultimode = new Multimode();
        Rule newRuleQ = new Rule();
        newRuleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        newRuleQ.setValue("my new multimode value");
        newRuleQ.setType(ValueTypeEnum.VTL);
        Rules newRulesQ = new Rules();
        newRulesQ.getRules().add(newRuleQ);
        newMultimode.setQuestionnaire(newRulesQ);
        newMultimode.setLeaf(new Rules());
        boolean isCreated = multimodeService.upsertQuestionnaireMultimode("multimode-id", newMultimode);

        // Then the multimode is updated
        assertFalse(isCreated);
        assertThat(multimodeService.getQuestionnaireMultimode("multimode-id")).usingRecursiveComparison().isEqualTo(newMultimode);
    }

    @Test
    @DisplayName("Should create questionnaire multimode")
    void upsertQuestionnaireMultimode_success_created() throws Exception {
        // Given a questionnaire in VTL with a roundabout and no multimode
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/loopRoundabout.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        assertNull(multimodeService.getQuestionnaireMultimode("m7d5k0hy"));

        // When we update the questionnaire's multimode
        Multimode newMultimode = new Multimode();
        Rule newRuleQ = new Rule();
        newRuleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        newRuleQ.setValue("my new multimode value");
        newRuleQ.setType(ValueTypeEnum.VTL);
        Rules newRulesQ = new Rules();
        newRulesQ.getRules().add(newRuleQ);
        newMultimode.setQuestionnaire(newRulesQ);
        newMultimode.setLeaf(new Rules());
        boolean isCreated = multimodeService.upsertQuestionnaireMultimode("m7d5k0hy", newMultimode);

        // Then the multimode is updated
        assertTrue(isCreated);
        assertThat(multimodeService.getQuestionnaireMultimode("m7d5k0hy")).usingRecursiveComparison().isEqualTo(newMultimode);
    }

    @Test
    @DisplayName("Should delete questionnaire multimode")
    void deleteQuestionnaireMultimode_success() throws Exception {
        // Given a questionnaire in VTL with a roundabout and a multimode
        Questionnaire mockQuestionnaire = loadQuestionnaireFromResources("service/multimode.json");
        String mockQuestionnaireString = PoguesSerializer.questionnaireJavaToString(mockQuestionnaire);
        JsonNode mockQuestionnaireJSON = jsonStringtoJsonNode(mockQuestionnaireString);
        questionnaireService.createQuestionnaire(mockQuestionnaireJSON);
        assertNotNull(multimodeService.getQuestionnaireMultimode("multimode-id"));

        // When we delete the multimode
        assertDoesNotThrow(() -> multimodeService.deleteQuestionnaireMultimode("multimode-id"));

        // Then the multimode is deleted
        assertNull(multimodeService.getQuestionnaireMultimode("multimode-id"));
    }

}
