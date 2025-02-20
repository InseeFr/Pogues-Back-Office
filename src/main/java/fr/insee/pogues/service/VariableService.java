package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
public class VariableService {

    private QuestionnairesService questionnairesService;

    private CodesListService codesListService;

    @Autowired
    public VariableService(QuestionnairesService questionnairesService, CodesListService codesListService){
        this.questionnairesService = questionnairesService;
        this.codesListService = codesListService;
    }

    public void updateQuestionAndVariablesAccordingToCodesList(Questionnaire questionnaire, String updatedCodeListId){
        CodeList codeList = questionnaire.getCodeLists().getCodeList().stream().filter(cL -> updatedCodeListId.equals(cL.getId())).findFirst().get();
        // handleOnly MULTIPLE_CHOICE question for now
        List<ComponentType> questionsToModify = codesListService
                .getListOfQuestionWhereCodesListIsUsed(questionnaire, updatedCodeListId).stream()
                .filter(componentType -> ((QuestionType) componentType).getQuestionType().equals(QuestionTypeEnum.MULTIPLE_CHOICE)).toList();
        // retrieve collected variables of multipleQuestion
        List<String> oldVariables = questionsToModify.stream()
                .map(componentType ->  getVariableOfMultipleQuestion((QuestionType) componentType))
                .flatMap(Collection::stream)
                .toList();
        // remove old variables
        questionnaire.getVariables().getVariable().removeIf(variable -> oldVariables.contains(variable.getId()));

        // generate response first with CollectedVariableReference id generated
        List<ResponseType> responses;


        // generate new variable according to codeList
        List<VariableType> newVariables = questionsToModify.stream()
                .map(componentType -> computeNewVariableAccordingToCodeListMultipleChoice(codeList, componentType.getName()))
                .flatMap(Collection::stream)
                .toList();

        // add new variables
        questionnaire.getVariables().getVariable().addAll(newVariables);

    }

    private List<String> getVariableOfMultipleQuestion(QuestionType multipleQuestion){
        if(!multipleQuestion.getQuestionType().equals(QuestionTypeEnum.MULTIPLE_CHOICE)) return List.of();
        return multipleQuestion.getResponse().stream()
                .map(responseType -> responseType.getCollectedVariableReference())
                .toList();
    }

    private VariableType buildBooleanVariableFromCode(CodeType codeType, String name){
        CollectedVariableType collectedVariableType = new CollectedVariableType();
        collectedVariableType.setId(UUID.randomUUID().toString());
        collectedVariableType.setName(name);
        collectedVariableType.setLabel(String.format("%s - %s",codeType.getValue(), codeType.getLabel()));
        DatatypeType booleanType = new BooleanDatatypeType();
        booleanType.setTypeName(DatatypeTypeEnum.BOOLEAN);
        collectedVariableType.setDatatype(booleanType);
        return collectedVariableType;
    }

    public List<VariableType> computeNewVariableAccordingToCodeListMultipleChoice(CodeList codeList, String questionName){
        return IntStream.range(0, codeList.getCode().size())
                .mapToObj(index -> buildBooleanVariableFromCode(codeList.getCode().get(index), String.format("%s%d",questionName,index+1)))
                .toList();
    }


    private Questionnaire retrieveQuestionnaireWithId(String id) throws Exception {
        return PoguesDeserializer.questionnaireToJavaObject(questionnairesService.getQuestionnaireByID(id));
    }

    private void updateQuestionnaireInDataBase(Questionnaire questionnaire) throws Exception {
        questionnairesService.updateQuestionnaire(
                questionnaire.getId(),
                jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire)));
    }

}
