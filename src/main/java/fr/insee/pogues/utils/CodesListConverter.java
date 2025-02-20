package fr.insee.pogues.utils;


import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.codeList.Code;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;

import java.util.ArrayList;
import java.util.List;

public class CodesListConverter {

    private CodesListConverter() {}

    private static List<CodeType> convertFromCodeListDTDToCodeTypeWithParent(Code code){
        List<CodeType> codeTypes = new ArrayList<>();
        CodeType codeType = new CodeType();
        codeType.setValue(code.getValue());
        codeType.setLabel(code.getLabel());
        codeTypes.add(codeType);
        if(code.getCodes() != null){
            List<CodeType> subCodeTypes = code.getCodes().stream().map(subCode -> {
                CodeType subCodeType = new CodeType();
                subCodeType.setValue(subCode.getValue());
                subCodeType.setLabel(subCode.getLabel());
                subCodeType.setParent(code.getValue());
                return subCodeType;
            }).toList();
            codeTypes.addAll(subCodeTypes);
        }
        return codeTypes;
    }

    public static CodeList convertFromCodeListDTDtoCodeListModel(CodesList codesListDtd){
        CodeList codeList = new CodeList();
        codeList.setId(codesListDtd.getId());
        codeList.setLabel(codesListDtd.getLabel());
        List<CodeType> codeTypes = new ArrayList<>();
        codesListDtd.getCodes().stream().forEach(code ->
            codeTypes.addAll(convertFromCodeListDTDToCodeTypeWithParent(code))
        );
        codeList.getCode().addAll(codeTypes);
        return  codeList;
    }




}
