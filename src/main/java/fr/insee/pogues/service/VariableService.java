package fr.insee.pogues.service;

import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.VariableNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.Questionnaire.Iterations;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.PoguesModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static fr.insee.pogues.utils.ListUtils.replaceElementInListAccordingToCondition;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.PoguesModelUtils.getLinkedLoopReferenceName;

/**
 * Variable Service used to fetch or update variables in questionnaires.
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

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByVersionId(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }

    /**
     * Fetch the variables of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the variables from
     * @throws Exception Could not read from or write in the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<VariableType> getQuestionnaireVariables(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getQuestionnaireVariables(questionnaire);
    }

    /**
     * Fetch the variables of a questionnaire's version.
     * @param versionId ID of the questionnaire's version to fetch the variables from
     * @throws Exception There was an error when fetching the questionnaire from the DB
     */
    public List<VariableType> getVersionVariables(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        return getQuestionnaireVariables(questionnaire);
    }

    /**
     * Get the questionnaire's variables and, if they have a scope, compute the scope name instead of the id.
     * @param questionnaire Questionnaire from which we want the variables
     * @return Questionnaire's variables with a readable scope.
     * @throws IllegalIterationException 
     */
    private List<VariableType> getQuestionnaireVariables(Questionnaire questionnaire) throws IllegalIterationException {
        List<VariableType> variables = questionnaire.getVariables().getVariable().stream().toList();
        for (VariableType variable : variables) {
            computeScopeNameFromScopeId(variable, questionnaire);
        }
        return variables;
    }

    /**
     * <p>Compute the scope name instead of the id. If no related scope is found, do nothing.</p>
     * <p>In the case of a linked loop, we use the iterable reference name.</p>
     * @param variable Variable to update
     * @param iterations Iterations in which we will find the scope name
     */
    private void computeScopeNameFromScopeId(VariableType variable, Questionnaire questionnaire) throws IllegalIterationException {
        Iterations iterations = questionnaire.getIterations();
        if (iterations == null) return;

        String scopeId = variable.getScope();
        if (scopeId == null) return;

        Optional<IterationType> iteration = iterations.getIteration().stream().filter(v -> {
            try {
                return PoguesModelUtils.isIterationRelatedToScopeId(v, scopeId);
            } catch (IllegalIterationException e) {
                return false;
            }
        }).findFirst();
        if (iteration.isPresent()) {
            if (PoguesModelUtils.isLinkedLoop(iteration.get())) {
                // If the related scope is associated to a linked loop, we use
                // the iterable reference name.
                variable.setScope(getLinkedLoopReferenceName(questionnaire, iteration.get()));
            } else {
                variable.setScope(iteration.get().getName());
            }
        }
    }

    /**
     * Update or create a new variable in the questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param variable Variable to upsert
     * @return Whether the variable has been created
     * @throws Exception Could not read the DB
     * @throws PoguesException Questionnaire not found
     */
    public boolean upsertQuestionnaireVariable(String questionnaireId, VariableType variable) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        boolean isCreated = upsertQuestionnaireVariable(questionnaire, variable);
        updateQuestionnaireInDataBase(questionnaire);
        return isCreated;
    }

    private boolean upsertQuestionnaireVariable(Questionnaire questionnaire, VariableType variable) {
        List<VariableType> variables = questionnaire.getVariables().getVariable();
        return upsertVariable(variables, variable);
    }

    /**
     * Update or create a new variable in the variable list.
     * @param existingVariables List of variables we want to add our variable to
     * @param variable Variable to create or update
     * @return Whether the variable was created
     */
    private boolean upsertVariable(List<VariableType> existingVariables, VariableType variable) {
        String variableId = variable.getId();
        if (existingVariables.stream().noneMatch(codeList -> Objects.equals(variableId, codeList.getId()))) {
            existingVariables.add(variable);
            return true;
        }

        updateVariable(existingVariables, variableId, variable);
        return false;
    }

    /**
     * Update an existing variable in the variable list.
     * @param existingVariables List of variables we want to add our variable to
     * @param variable Variable to update
     */
    private void updateVariable(List<VariableType> existingVariables, String variableId, VariableType variable) {
        replaceElementInListAccordingToCondition(
                existingVariables,
                existingVariable -> Objects.equals(variableId, existingVariable.getId()),
                variable,
                v -> v);
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
