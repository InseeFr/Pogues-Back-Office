package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.IQuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.mapper.CodesListMapper;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.question.Common;
import fr.insee.pogues.controller.error.ErrorCode;
import fr.insee.pogues.model.dto.codeslists.CodesListDTO;
import fr.insee.pogues.model.dto.codeslists.ExtendedCodesListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fr.insee.pogues.mapper.CodesListMapper.toModel;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.ListUtils.replaceElementInListAccordingToCondition;
import static fr.insee.pogues.utils.model.CodesList.*;
import static fr.insee.pogues.utils.model.Variables.getNeededCollectedVariablesInQuestionnaire;
import static fr.insee.pogues.utils.model.question.MultipleChoice.updateMultipleChoiceQuestionAccordingToCodeList;
import static fr.insee.pogues.utils.model.question.Table.updateTableQuestionAccordingToCodeList;

/**
 * Variable Service used to fetch or update codes lists in questionnaires.
 */
@Service
@Slf4j
public class CodesListService {

    private final IQuestionnaireService questionnaireService;
    private final VersionService versionService;

    public CodesListService(IQuestionnaireService questionnaireService,
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
     * Fetch the codes lists of a questionnaire.
     * @param questionnaireId ID of the questionnaire to fetch the codes lists from
     * @throws Exception Could not read from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedCodesListDTO> getQuestionnaireCodesLists(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getCodesListsDTO(questionnaire);
    }

    /**
     * Fetch the codes lists of a questionnaire.
     * @param versionId ID of the questionnaire's version to fetch the codes lists from
     * @throws Exception Could not read from the DB
     * @throws PoguesException 404 questionnaire not found
     */
    public List<ExtendedCodesListDTO> getVersionCodesLists(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByVersionId(versionId);
        return getCodesListsDTO(questionnaire);
    }

    private List<ExtendedCodesListDTO> getCodesListsDTO(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(codeList -> !isNomenclatureCodeList(codeList))
                .map(CodesListMapper::toDTO)
                .map(codesList -> new ExtendedCodesListDTO(codesList, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, codesList.getId())))
                .toList();
    }

    /**
     * Update or create a new code list in the questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param idCodesList ID of the code list to upsert
     * @param codesListDTO New or updated code list
     * @return IDs of questions that have been updated by this update (null if it's a new code list)
     * @throws Exception
     */
    public List<String> upsertQuestionnaireCodesList(String questionnaireId, String idCodesList, CodesListDTO codesListDTO) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        List<String> updatedQuestionIds = upsertQuestionnaireCodesList(questionnaire, idCodesList, codesListDTO);
        updateQuestionnaireInDataBase(questionnaire);
        return updatedQuestionIds;
    }

    /**
     * @return the list of question's id updated (or null if created)
     */
    private List<String> upsertQuestionnaireCodesList(Questionnaire questionnaire, String idCodesList, CodesListDTO codesListDTO) {
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        boolean isCreated = upsertCodeList(codesLists, idCodesList, codesListDTO);
        if (isCreated) {
            return null;
        }
        return updateQuestionAndVariablesAccordingToCodesList(questionnaire, idCodesList);
    }

    /**
     * @return Whether the code list has been created
     */
    private boolean upsertCodeList(List<CodeList> existingCodeLists, String idCodesList, CodesListDTO codesListDtoToUpdate) {
        if (existingCodeLists.stream().noneMatch(codeList -> Objects.equals(idCodesList, codeList.getId()))) {
            existingCodeLists.add(toModel(codesListDtoToUpdate));
            return true;
        }

        codesListDtoToUpdate.setId(idCodesList);
        replaceElementInListAccordingToCondition(
                existingCodeLists,
                codeList -> Objects.equals(idCodesList, codeList.getId()),
                codesListDtoToUpdate,
                codesList -> toModel(codesListDtoToUpdate));
        return false;
    }

    /**
     * Delete the code list of a questionnaire.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param codesListId ID of the code list to delete
     * @throws Exception 404 questionnaire or code list not found
     * @throws CodesListException 400 code list has related questions
     */
    public void deleteQuestionnaireCodeList(String questionnaireId, String codesListId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        deleteQuestionnaireCodeList(questionnaire, codesListId);
        updateQuestionnaireInDataBase(questionnaire);
    }

    private void deleteQuestionnaireCodeList(Questionnaire questionnaire, String codesListId) throws CodesListException {
        List<String> questionsName = getListOfQuestionNameWhereCodesListIsUsed(questionnaire, codesListId);
        if (!questionsName.isEmpty()) {
            String message = String.format("CodesList with id %s is required.", codesListId);
            throw new CodesListException(400, ErrorCode.CODE_LIST_RELATED_QUESTIONS_NAME, message, null, questionsName);
        }

        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        boolean deleted = codesLists.removeIf(codeList -> codesListId.equals(codeList.getId()));
        if (!deleted) {
            String message = String.format("CodesList with id %s doesn't exist in questionnaire", codesListId);
            throw new CodesListException(404, ErrorCode.CODE_LIST_NOT_FOUND, "Not found", message, null);
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

    private List<String> updateQuestionAndVariablesAccordingToCodesList(Questionnaire questionnaire, String updatedCodeListId) {
        // Retrieve updatedCodeList in questionnaire
        CodeList codeList = questionnaire.getCodeLists().getCodeList().stream()
                .filter(cL -> updatedCodeListId.equals(cL.getId()))
                .findFirst()
                .orElseThrow();
        // Just retrieve MULTIPLE_CHOICE and TABLE questions
        List<QuestionType> questionsToModify = getListOfQuestionWhereCodesListIsUsed(questionnaire, updatedCodeListId);
        // Clear Clarification question for concerned question
        questionsToModify.forEach(Common::removeClarificationQuestion);
        // Clear orphan CodeList filters for concerned question
        questionsToModify.forEach(question -> Common.removeCodeListFilters(question, codeList));
        // modify Multiple and Table question and get there new Variables
        List<QuestionType> multipleAndTableQuestion = questionsToModify.stream()
                .filter(questionType -> {
                    QuestionTypeEnum questionTypeEnum = questionType.getQuestionType();
                    return QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionTypeEnum) || QuestionTypeEnum.TABLE.equals(questionTypeEnum);
                })
                .toList();
        List<VariableType> variables = multipleAndTableQuestion.stream()
                .map(questionType -> {
                    QuestionTypeEnum questionTypeEnum = questionType.getQuestionType();
                    if (QuestionTypeEnum.MULTIPLE_CHOICE.equals(questionTypeEnum))
                        return updateMultipleChoiceQuestionAccordingToCodeList(questionType, codeList, questionnaire.getCodeLists().getCodeList());
                    return updateTableQuestionAccordingToCodeList(questionType, codeList, questionnaire.getCodeLists().getCodeList());
                })
                .flatMap(Collection::stream)
                .toList();
        // add new variables to questionnaire
        questionnaire.getVariables().getVariable().addAll(variables);
        // Delete only collected variables that are not referenced in response
        List<String> neededCollectedVariables = getNeededCollectedVariablesInQuestionnaire(questionnaire);
        questionnaire.getVariables().getVariable()
                .removeIf(variableType -> variableType instanceof CollectedVariableType
                        && !neededCollectedVariables.contains(variableType.getId()));
        return questionsToModify.stream().map(ComponentType::getId).toList();
    }

}
