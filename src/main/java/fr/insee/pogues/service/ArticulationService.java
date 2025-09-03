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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.PoguesModelUtils.getQuestionnaireRoundabout;

/**
 * <p>Service used to fetch, update or delete the articulation of a questionnaire.</p>
 * <p>A questionnaire can only use this feature if its language formula is VTL.</p>
 */
@Service
@Slf4j
public class ArticulationService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;

    public ArticulationService(IQuestionnaireService questionnaireService,
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
        if (!isQuestionnaireFormulaLanguageInVTL(questionnaire)) {
            String message = String.format("Questionnaire with id %s has the formula language %s and not VTL", questionnaire.getId(), questionnaire.getFormulasLanguage());
            throw new QuestionnaireFormulaLanguageNotVTLException(message);
        }
        if (getQuestionnaireRoundabout(questionnaire).isEmpty()) {
            String message = String.format("Questionnaire with id %s does not have a roundabout", questionnaire.getId());
            throw new QuestionnaireRoundaboutNotFoundException(message);
        }
    }

    /**
     * Check if the questionnaire's formula language is VTL.
     * @param questionnaire Questionnaire to check
     */
    private boolean isQuestionnaireFormulaLanguageInVTL(Questionnaire questionnaire) {
        return questionnaire.getFormulasLanguage().equals(FormulasLanguageEnum.VTL);
    }

    /**
     * Fetch the articulation of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the articulation from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public Articulation getQuestionnaireArticulation(String questionnaireId)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException, QuestionnaireRoundaboutNotFoundException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        return getQuestionnaireArticulation(questionnaire);
    }

    /**
     * Fetch the articulation of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the variables from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public Articulation getVersionArticulation(UUID versionId) throws Exception, QuestionnaireFormulaLanguageNotVTLException {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        checkQuestionnaireCompatibility(questionnaire);
        return getQuestionnaireArticulation(questionnaire);
    }

    /**
     * Get the questionnaire's articulation.
     * @param questionnaire Questionnaire from which we want the articulation
     * @return Questionnaire's articulation.
     */
    private Articulation getQuestionnaireArticulation(Questionnaire questionnaire) {
        return questionnaire.getArticulation();
    }

    /**
     * Update or create a new variable in the questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param articulation Articulation to upsert
     * @return Whether the articulation has been created (vs updated)
     * @throws Exception Could not read the DB
     * @throws PoguesException Questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public boolean upsertQuestionnaireArticulation(String questionnaireId, Articulation articulation)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException, QuestionnaireRoundaboutNotFoundException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        boolean isCreated = upsertQuestionnaireArticulation(questionnaire, articulation);
        updateQuestionnaireInDataBase(questionnaire);
        return isCreated;
    }

    private boolean upsertQuestionnaireArticulation(Questionnaire questionnaire, Articulation articulation) {
        boolean isCreated = questionnaire.getArticulation() == null;
        questionnaire.setArticulation(articulation);
        return isCreated;
    }

    /**
     * Delete the articulation of a questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @throws Exception 404 questionnaire not found
     * @throws VariableNotFoundException There is no variable with the provided ID
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public void deleteQuestionnaireArticulation(String questionnaireId)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException, QuestionnaireRoundaboutNotFoundException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        deleteQuestionnaireArticulation(questionnaire);
        updateQuestionnaireInDataBase(questionnaire);
    }

    /**
     * Delete the articulation of a questionnaire.
     * @param questionnaire Questionnaire to update
     */
    private void deleteQuestionnaireArticulation(Questionnaire questionnaire) {
        questionnaire.setArticulation(null);
    }

    /**
     * Fetch the variables related to the articulation of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the articulation from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     * @throws QuestionnaireFormulaLanguageNotVTLException Questionnaire is not in VTL, it cannot use this feature
     * @throws QuestionnaireRoundaboutNotFoundException Questionnaire does not have a roundabout, it cannot use this feature
     */
    public List<VariableType> getQuestionnaireArticulationVariables(String questionnaireId)
            throws Exception, QuestionnaireFormulaLanguageNotVTLException, QuestionnaireRoundaboutNotFoundException {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        checkQuestionnaireCompatibility(questionnaire);
        return getQuestionnaireArticulationVariables(questionnaire);
    }

    private List<VariableType> getQuestionnaireArticulationVariables(Questionnaire questionnaire) {
        Optional<RoundaboutType> optionalRoundabout = getQuestionnaireRoundabout(questionnaire);
        // roundabout cannot be null since we checked if the questionnaire has one beforehand
        assert optionalRoundabout.isPresent();

        String roundaboutId = optionalRoundabout.get().getLoop().getIterableReference();
        return questionnaire.getVariables().getVariable().stream().filter(v -> roundaboutId.equals(v.getScope())).toList();
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
