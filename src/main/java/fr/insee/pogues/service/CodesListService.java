package fr.insee.pogues.service;

import fr.insee.pogues.exception.CodesListException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnaireService;
import fr.insee.pogues.persistence.service.VersionService;
import fr.insee.pogues.webservice.mapper.CodesListMapper;
import fr.insee.pogues.utils.DateUtils;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import fr.insee.pogues.utils.model.question.Common;
import fr.insee.pogues.webservice.error.ErrorCode;
import fr.insee.pogues.webservice.model.dto.codeslists.CodesListDTO;
import fr.insee.pogues.webservice.model.dto.codeslists.ExtendedCodesListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static fr.insee.pogues.webservice.mapper.CodesListMapper.convertFromCodeListDTOtoCodeListModel;
import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.ListUtils.replaceElementInListAccordingToCondition;
import static fr.insee.pogues.utils.model.CodesList.*;
import static fr.insee.pogues.utils.model.Variables.getNeededCollectedVariablesInQuestionnaire;
import static fr.insee.pogues.utils.model.question.MultipleChoice.updateMultipleChoiceQuestionAccordingToCodeList;
import static fr.insee.pogues.utils.model.question.Table.updateTableQuestionAccordingToCodeList;

@Service
@Slf4j
public class CodesListService {

    private final QuestionnaireService questionnaireService;
    private final VersionService versionService;

    public CodesListService(QuestionnaireService questionnaireService,
                            VersionService versionService) {
        this.questionnaireService = questionnaireService;
        this.versionService = versionService;
    }

    /**
     * Update the questionnaire with a new code list or an updated existing one.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param idCodesList ID of the code list to upsert
     * @param codesListDTO New or updated code list
     * @return IDs of questions that have been updated by this update (null if it's a new code list)
     * @throws Exception
     */
    public List<String> updateOrAddCodeListToQuestionnaire(String questionnaireId, String idCodesList, CodesListDTO codesListDTO) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        List<String> updatedQuestionIds = updateOrAddCodeListToQuestionnaire(questionnaire, idCodesList, codesListDTO);
        updateQuestionnaireInDataBase(questionnaire);
        return updatedQuestionIds;
    }

    /**
     * @param questionnaire
     * @param idCodesList
     * @param codesListDTO
     * @return the list of question's id updated (or null if created)
     * @throws Exception
     */
    public List<String> updateOrAddCodeListToQuestionnaire(Questionnaire questionnaire, String idCodesList, CodesListDTO codesListDTO) {
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        boolean created = updateOrAddCodeListDTO(codesLists, idCodesList, codesListDTO);
        return !created
                ? updateQuestionAndVariablesAccordingToCodesList(questionnaire, idCodesList)
                : null;
    }

    /**
     * Update the questionnaire by removing an existing code list.
     * It will update the questionnaire's last updated date.
     * @param questionnaireId ID of the questionnaire to update
     * @param codesListId ID of the code list to delete
     * @throws Exception
     */
    public void deleteCodeListOfQuestionnaireById(String questionnaireId, String codesListId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        deleteCodeListOfQuestionnaire(questionnaire, codesListId);
        updateQuestionnaireInDataBase(questionnaire);
    }

    public void deleteCodeListOfQuestionnaire(Questionnaire questionnaire, String codesListId) throws CodesListException {
        List<String> questionsName = getListOfQuestionNameWhereCodesListIsUsed(questionnaire, codesListId);
        if (!questionsName.isEmpty()) {
            throw new CodesListException(400, ErrorCode.CODE_LIST_RELATED_QUESTIONS_NAME, String.format("CodesList with id %s is required.", codesListId), null, questionsName);

        }
        List<fr.insee.pogues.model.CodeList> codesLists = questionnaire.getCodeLists().getCodeList();
        removeCodeListDTO(codesLists, codesListId);
    }

    private Questionnaire retrieveQuestionnaireByQuestionnaireId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnaireService.getQuestionnaireByID(id));
    }

    private Questionnaire retrieveQuestionnaireByIdVersion(UUID versionId) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(versionService.getVersionDataByVersionId(versionId));
    }

    /**
     * Set the questionnaire last updated date as now and save it in the DB.
     * @param questionnaire Questionnaire to update
     * @throws Exception
     */
    private void updateQuestionnaireInDataBase(Questionnaire questionnaire) throws Exception {
        questionnaire.setLastUpdatedDate(DateUtils.getIsoDateFromInstant(Instant.now()));
        questionnaireService.updateQuestionnaire(
                questionnaire.getId(),
                jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire)));
    }

    /**
     * @param existingCodeLists
     * @param idCodesList
     * @param codesListDtoToUpdate
     * @return true if new codeList is created
     */
    boolean updateOrAddCodeListDTO(List<CodeList> existingCodeLists, String idCodesList, CodesListDTO codesListDtoToUpdate) {
        if (existingCodeLists.stream().noneMatch(codeList -> Objects.equals(idCodesList, codeList.getId()))) {
            addCodeListDTO(existingCodeLists, codesListDtoToUpdate);
            return true;
        }
        codesListDtoToUpdate.setId(idCodesList);
        replaceElementInListAccordingToCondition(
                existingCodeLists,
                codeList -> Objects.equals(idCodesList, codeList.getId()),
                codesListDtoToUpdate,
                codesList -> convertFromCodeListDTOtoCodeListModel(codesListDtoToUpdate));
        return false;
    }


    void addCodeListDTO(List<CodeList> existingCodeLists, CodesListDTO codesListDtoToAdd) {
        existingCodeLists.add(convertFromCodeListDTOtoCodeListModel(codesListDtoToAdd));
    }

    void removeCodeListDTO(List<CodeList> existingCodeLists, String id) throws CodesListException {
        boolean deleted = existingCodeLists.removeIf(codeList -> id.equals(codeList.getId()));
        if (!deleted)
            throw new CodesListException(404, ErrorCode.CODE_LIST_NOT_FOUND, "Not found", String.format("CodesList with id %s doesn't exist in questionnaire", id), null);
    }

    List<String> updateQuestionAndVariablesAccordingToCodesList(Questionnaire questionnaire, String updatedCodeListId) {
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

    public List<ExtendedCodesListDTO> getCodesListsDTO(Questionnaire questionnaire) {
        return questionnaire.getCodeLists().getCodeList().stream()
                .filter(codeList -> !isNomenclatureCodeList(codeList))
                .map(CodesListMapper::convertFromCodeListModelToCodeListDTO)
                .map(codesList -> new ExtendedCodesListDTO(codesList, getListOfQuestionNameWhereCodesListIsUsed(questionnaire, codesList.getId())))
                .toList();
    }

    public List<ExtendedCodesListDTO> getCodesListsDTOByQuestionnaireId(String questionnaireId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByQuestionnaireId(questionnaireId);
        return getCodesListsDTO(questionnaire);
    }

    public List<ExtendedCodesListDTO> getCodesListsDTOByVersionId(UUID versionId) throws Exception {
        Questionnaire questionnaire = retrieveQuestionnaireByIdVersion(versionId);
        return getCodesListsDTO(questionnaire);
    }

}
