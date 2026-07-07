package fr.insee.pogues.utils;

import fr.insee.pogues.model.*;

import java.util.ArrayList;
import java.util.List;

import static fr.insee.pogues.utils.model.question.Common.getNewUniqueId;

public class ModelCreatorUtils {

    public static ResponseType createResponse(DatatypeTypeEnum datatype){
        ResponseType response = new ResponseType();
        response.setId(getNewUniqueId());
        response.setCollectedVariableReference(getNewUniqueId());
        DatatypeType datatypeType = new DateDatatypeType();
        datatypeType.setTypeName(datatype);
        response.setDatatype(datatypeType);
        return response;
    }

    public static DimensionType createSimpleMeasureDimension(String label){
        DimensionType measure = new DimensionType();
        measure.setDimensionType(DimensionTypeEnum.MEASURE);
        measure.setLabel(label);
        return measure;
    }

    public static CodeType initFakeCodeType(String value, String label, String parent){
        CodeType codeType = new CodeType();
        codeType.setValue(value);
        codeType.setLabel(label);
        codeType.setParent(parent);
        return codeType;
    }

    public static CodeList initFakeCodeList(String id, String label){
       CodeList codeList = new CodeList();
        codeList.setId(id);
        codeList.setLabel(label);
        codeList.getCode().addAll(List.of(
                initFakeCodeType(id+"-v1", "label 1", null),
                initFakeCodeType(id+"-v2", "label 2", null),
                initFakeCodeType(id+"-v3", "label 3", null),
                initFakeCodeType(id+"-v4", "label 4", null)
        ));
        return codeList;
    }

    public static List<CodeList> initFakeCodeLists(int size){
        List<CodeList> codeLists = new ArrayList<>();
        for(int i = 0; i < size; i++){
            codeLists.add(initFakeCodeList("code-list-"+i, "Super code list "+i));
        }
        return codeLists;
    }

    public static CodeList initFakeNomenclature(String id, String label){
        CodeList codeList = new CodeList();
        codeList.setId(id);
        codeList.setLabel(label);
        codeList.setName("Name "+label);
        SuggesterParametersType suggesterParameters = new SuggesterParametersType();
        suggesterParameters.setId("sugg-id"+id);
        codeList.setSuggesterParameters(suggesterParameters);
        codeList.setUrn("urn::sugg-"+id);
        return codeList;
    }

    public static QuestionType createQuestionWithCodeList(String codeListId){
        ResponseType response = createResponse(DatatypeTypeEnum.TEXT);
        response.setCodeListReference(codeListId);
        QuestionType question = new QuestionType();
        question.setQuestionType(QuestionTypeEnum.SINGLE_CHOICE);
        question.setName("Question-Name"+codeListId);
        question.getLabel().add("Question label "+codeListId);
        question.setId(getNewUniqueId());
        question.getResponse().add(response);
        return question;
    }

    public static CollectedVariableType createCollectedVariableAccordingToResponse(ResponseType response){
        CollectedVariableType variable = new CollectedVariableType();
        variable.setId(response.getVariableReference());
        variable.setName("VARIABLE");
        variable.setDatatype(response.getDatatype());
        variable.setLabel("Variable label");
        variable.setCodeListReference(response.getCodeListReference());
        return variable;
    }
}
