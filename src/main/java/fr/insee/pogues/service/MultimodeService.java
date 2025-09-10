package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.QuestionnaireFormulaLanguageNotVTLException;
import fr.insee.pogues.exception.QuestionnaireRoundaboutNotFoundException;
import fr.insee.pogues.exception.VariableNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.PoguesModelUtils.*;

/**
 * <p>Service used to fetch, update or delete the multimode of a questionnaire.</p>
 * <p>A questionnaire can only use this feature if its language formula is VTL.</p>
 */
@Service
@Slf4j
public class MultimodeService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;

    public MultimodeService(IQuestionnaireService questionnaireService,
                            VersionService versionService) {
        this.questionnaireService = questionnaireService;
        this.versionService = versionService;
    }

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }

    /**
     * Check if the questionnaire can use the feature (i.e. is its formula language VTL, and it has a roundabout).
     * @param questionnaire Questionnaire to check
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire's formula language is not in VTL
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout
     */
    private void checkQuestionnaireCompatibility(Questionnaire questionnaire) throws QuestionnaireFormulaLanguageNotVTLException, QuestionnaireRoundaboutNotFoundException {
        if (!isQuestionnaireFormulaLanguageVTL(questionnaire)) {
            String message = String.format("Questionnaire with id %s has the formula language %s and not VTL", questionnaire.getId(), questionnaire.getFormulasLanguage());
            throw new QuestionnaireFormulaLanguageNotVTLException(message);
        }
    }

    /**
     * Fetch the multimode of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the multimode from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     */
    public Multimode getQuestionnaireMultimode(String questionnaireId)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        return getQuestionnaireMultimode(questionnaire);
    }

    /**
     * Fetch the multimode of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the variables from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public Multimode getVersionMultimode(UUID versionId) throws Exception, QuestionnaireFormulaLanguageNotVTLException {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        checkQuestionnaireCompatibility(questionnaire);
        return getQuestionnaireMultimode(questionnaire);
    }

    /**
     * Get the questionnaire's multimode.
     * @param questionnaire Questionnaire from which we want the multimode
     * @return Questionnaire's multimode.
     */
    private Multimode getQuestionnaireMultimode(Questionnaire questionnaire) {
        return questionnaire.getMultimode();
    }

    /**
     * Update or create a new multimode in the questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param multimode Multimode to upsert
     * @return Whether the multimode has been created (vs updated)
     * @throws Exception Could not read the DB
     * @throws PoguesException Questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     */
    public boolean upsertQuestionnaireMultimode(String questionnaireId, Multimode multimode)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        boolean isCreated = upsertQuestionnaireMultimode(questionnaire, multimode);
        updateQuestionnaireInDataBase(questionnaire);
        return isCreated;
    }

    private boolean upsertQuestionnaireMultimode(Questionnaire questionnaire, Multimode multimode) {
        boolean isCreated = questionnaire.getMultimode() == null;
        questionnaire.setMultimode(multimode);
        return isCreated;
    }

    /**
     * Delete the multimode of a questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @throws Exception 404 questionnaire not found
     * @throws VariableNotFoundException There is no variable with the provided ID
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     */
    public void deleteQuestionnaireMultimode(String questionnaireId)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        deleteQuestionnaireMultimode(questionnaire);
        updateQuestionnaireInDataBase(questionnaire);
    }

    /**
     * Delete the multimode of a questionnaire.
     * @param questionnaire Questionnaire to update
     */
    private void deleteQuestionnaireMultimode(Questionnaire questionnaire) {
        questionnaire.setMultimode(null);
    }

    /**
     * Set the questionnaire last updated date as now and save it in the DB.
     * @param questionnaire Questionnaire to update
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     */
    private void updateQuestionnaireInDataBase(Questionnaire questionnaire) throws Exception {
        questionnaire.setLastUpdatedDate(DateUtils.getIsoDateFromInstant(Instant.now()));
        questionnaireService.updateQuestionnaire(
                questionnaire.getId(),
                jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire)));
    }

}
