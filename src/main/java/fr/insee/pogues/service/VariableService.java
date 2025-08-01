package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.VariableInvalidModelException;
import fr.insee.pogues.exception.VariableNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.VariablesConverter;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.webservice.model.dtd.variables.Variable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fr.insee.pogues.utils.ListUtils.replaceElementInListAccordingToCondition;
import static fr.insee.pogues.utils.VariablesConverter.convertFromDTDtoModel;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Variable Service used to fetch or update variables between Pogues UI and API.
 * 
 */
@Service
@Slf4j
public class VariableService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;

    public VariableService(IQuestionnaireService questionnaireService,
                           VersionService versionService) {
        this.questionnaireService = questionnaireService;
        this.versionService = versionService;
    }

    /**
     * Update or create a new variable in the questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param variable Variable to upsert
     * @return Whether the variable has been created
     * @throws Exception Could not read the DB
     * @throws PoguesException Questionnaire not found
     * @throws VariableInvalidModelException The provided variable has an invalid type
     */
    public boolean upsertQuestionnaireVariable(String questionnaireId, Variable variable) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        boolean isCreated = upsertQuestionnaireVariable(questionnaire, variable);
        updateQuestionnaireInDataBase(questionnaire);
        return isCreated;
    }

    private boolean upsertQuestionnaireVariable(Questionnaire questionnaire, Variable variable) throws VariableInvalidModelException {
        List<VariableType> variables = questionnaire.getVariables().getVariable();
        return upsertVariableDTD(variables, variable);
    }

    /**
     * Update or create a new variable in the variable list.
     * @param existingVariables List of variables we want to add our variable to
     * @param variable Variable to create or update
     * @return Whether the variable was created
     * @throws VariableInvalidModelException The provided variable has an invalid type
     */
    private boolean upsertVariableDTD(List<VariableType> existingVariables, Variable variable) throws VariableInvalidModelException {
        String variableId = variable.getId();
        if (existingVariables.stream().noneMatch(codeList -> Objects.equals(variableId, codeList.getId()))) {
            insertVariableDTD(existingVariables, variable);
            return true;
        }

        updateVariableDTD(existingVariables, variableId, variable);
        return false;
    }

    /**
     * Create a new variable in the variable list.
     * @param existingVariables List of variables we want to add our variable to
     * @param variable Variable to create
     * @throws VariableInvalidModelException The provided variable has an invalid type
     */
    private void insertVariableDTD(List<VariableType> existingVariables, Variable variable) throws VariableInvalidModelException {
        VariableType variableModel = convertFromDTDtoModel(variable);
        existingVariables.add(variableModel);
    }

    /**
     * Update an existing variable in the variable list.
     * @param existingVariables List of variables we want to add our variable to
     * @param variable Variable to update
     * @throws VariableInvalidModelException The provided variable has an invalid type
     */
    @SneakyThrows
    private void updateVariableDTD(List<VariableType> existingVariables, String variableId, Variable variable) {
        replaceElementInListAccordingToCondition(
                existingVariables,
                existingVariable -> Objects.equals(variableId, existingVariable.getId()),
                variable,
                VariablesConverter::convertFromDTDtoModel);
    }

    /**
     * Delete the variable of a questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param variableId ID of the variable to delete
     * @throws Exception 404 questionnaire not found
     * @throws VariableNotFoundException There is no variable with the provided ID
     */
    public void deleteQuestionnaireVariable(String questionnaireId, String variableId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        deleteQuestionnaireVariable(questionnaire, variableId);
        updateQuestionnaireInDataBase(questionnaire);
    }

    /**
     * Delete the variable from a questionnaire.
     * @param questionnaire Questionnaire to update
     * @param variableId ID of the variable to delete
     * @throws VariableNotFoundException There is no variable with the provided ID
     */
    private void deleteQuestionnaireVariable(Questionnaire questionnaire, String variableId) throws VariableNotFoundException {
        List<VariableType> variables = questionnaire.getVariables().getVariable();
        boolean deleted = variables.removeIf(variable -> variableId.equals(variable.getId()));
        if (!deleted) {
            String message = String.format("Variable with id %s does not exist in the questionnaire", variableId);
            throw new VariableNotFoundException(message);
        }
    }

    /**
     * Set the questionnaire last updated date as now and save it in the DB.
     * @param questionnaire Questionnaire to update
     * @throws PoguesException Questionnaire not found
     * @throws Exception Could not read from or write in the DB
     */
    private void updateQuestionnaireInDataBase(Questionnaire questionnaire) throws Exception {
        questionnaire.setLastUpdatedDate(DateUtils.getIsoDateFromInstant(Instant.now()));
        questionnaireService.updateQuestionnaire(
                questionnaire.getId(),
                jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire)));
    }

    /**
     * Fetch the variables of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the variables from
     * @throws Exception Could not read from or write in the DB
     * @throws VariableInvalidModelException A variable of the questionnaire has an invalid type
     */
    public List<Variable> getQuestionnaireVariables(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getQuestionnaireVariablesDTD(questionnaire);
    }

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    /**
     * Fetch the variables of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the variables from
     * @throws Exception There was an error when fetching the questionnaire from the DB
     * @throws VariableInvalidModelException A variable of the questionnaire has an invalid type
     */
    public List<Variable> getVersionVariables(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByIdVersion(versionId);
        return getQuestionnaireVariablesDTD(questionnaire);
    }

    private Questionnaire retrieveQuestionnaireByIdVersion(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }

    @SneakyThrows
    private List<Variable> getQuestionnaireVariablesDTD(Questionnaire questionnaire) {
        return questionnaire.getVariables().getVariable().stream()
                .map(VariablesConverter::convertFromModelToDTD)
                .toList();
    }

}
