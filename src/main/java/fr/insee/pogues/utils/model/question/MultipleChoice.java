package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.*;

import java.util.List;
import java.util.stream.IntStream;

import static fr.insee.pogues.utils.model.CodesList.getOnlyCodesWithoutChild;
import static fr.insee.pogues.utils.model.Variables.buildBooleanVariableFromCode;
import static fr.insee.pogues.utils.model.question.Common.*;

public class MultipleChoice {

    public static List<VariableType> updateMultipleChoiceQuestionAccordingToCodeList(QuestionType questionType, CodeList updatedCodeList){
        // 1: re-create Response with new collectedVariableReference
        // 2: update "Mapping" inside "ResponseStructure" with id of response
        // 3: create Variables with id created in step 1
        // 4: return list all new Variables
        if(!questionType.getQuestionType().equals(QuestionTypeEnum.MULTIPLE_CHOICE)) return List.of();

        // Step 1
        List<CodeType> codesWithoutChild = getOnlyCodesWithoutChild(updatedCodeList);
        List<ResponseType> newResponses = codesWithoutChild.stream().map(codeType -> {
            ResponseType responseType = new ResponseType();
            DatatypeType booleanType = new BooleanDatatypeType();
            booleanType.setTypeName(DatatypeTypeEnum.BOOLEAN);
            responseType.setDatatype(booleanType);
            responseType.setId(getNewUniqueId());
            responseType.setCollectedVariableReference(getNewUniqueId());
            return responseType;
        }).toList();
        questionType.getResponse().clear();
        questionType.getResponse().addAll(newResponses);
        // step 2
        List<MappingType> newMappings = buildSimpleMappingForMultipleChoiceQuestion(newResponses);
        // no need to update dimension attribute (codeListRef is the same)
        questionType.getResponseStructure().getMapping().clear();
        questionType.getResponseStructure().getMapping().addAll(newMappings);
        // step 3
        List<VariableType> variables = IntStream.range(0, newResponses.size())
                .mapToObj(index -> buildBooleanVariableFromCode(
                        codesWithoutChild.get(index),
                        newResponses.get(index).getCollectedVariableReference(),
                        String.format("%s_%s",
                                questionType.getName(),
                                codesWithoutChild.get(index).getValue())))
                .toList();
        // step 4
        return variables;
    }
}
