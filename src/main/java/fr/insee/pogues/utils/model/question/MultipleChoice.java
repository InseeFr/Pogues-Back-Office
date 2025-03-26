package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.List;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.model.CodesList.getOnlyCodesWithoutChild;
import static fr.insee.pogues.utils.model.Variables.*;
import static fr.insee.pogues.utils.model.question.Common.*;

public class MultipleChoice {

    private MultipleChoice(){
        throw new IllegalStateException("Utility class");
    }

    private static ResponseType getResponseType(QuestionType multipleChoiceQuestion){
        if(!multipleChoiceQuestion.getQuestionType().equals(QuestionTypeEnum.MULTIPLE_CHOICE)) throw new IllegalArgumentException("Arg for this function must be a MultipleChoice component.");
        // in cas of MULTIPLE_CHOICE QUESTION, all responseType are identical (base on codeList (always the same for all codes, or based on BOOLEAN)
        return Common.cloneResponse(multipleChoiceQuestion.getResponse().getFirst());
    }

    public static List<VariableType> updateMultipleChoiceQuestionAccordingToCodeList(QuestionType questionType, CodeList updatedCodeList){
        // 1: re-create Response with new collectedVariableReference
        // 2: update "Mapping" inside "ResponseStructure" with id of response
        // 3: create Variables with id created in step 1
        // 4: return list all new Variables
        if(!questionType.getQuestionType().equals(QuestionTypeEnum.MULTIPLE_CHOICE)) return List.of();

        // Step 1
        List<CodeType> codesWithoutChild = getOnlyCodesWithoutChild(updatedCodeList);
        ResponseType responsePattern = getResponseType(questionType);
        List<ResponseType> newResponses = codesWithoutChild.stream().map(code -> createNewResponseFrom(responsePattern)).toList();
        questionType.getResponse().clear();
        questionType.getResponse().addAll(newResponses);
        // step 2
        List<MappingType> newMappings = buildSimpleMappingForMultipleChoiceQuestion(newResponses);
        // no need to update dimension attribute (codeListRef is the same)
        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);
        // step 3 & 4
        return IntStream.range(0, newResponses.size())
                .mapToObj(index -> buildCollectedVariableFromDataType(
                        newResponses.get(index).getDatatype(),
                        newResponses.get(index).getCollectedVariableReference(),
                        String.format(VARIABLE_FORMAT_MULTIPLE_CHOICE, questionType.getName(), index+1),
                        String.format(COLLECTED_LABEL_FORMAT, codesWithoutChild.get(index).getValue(), codesWithoutChild.get(index).getLabel()),
                        newResponses.get(index).getCodeListReference()))
                .toList();
    }
}
