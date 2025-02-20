package fr.insee.pogues.utils;


import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.codeList.Code;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;

import java.util.ArrayList;
import java.util.List;

public class CodesListConverter {

    private CodesListConverter() {}

    private static List<CodeType> convertFromCodeListDTDToCodeTypeWithParent(Code code, String parent){
        List<CodeType> codeTypes = new ArrayList<>();
        CodeType codeType = new CodeType();
        codeType.setValue(code.getValue());
        codeType.setLabel(code.getLabel());
        codeType.setParent(parent);
        codeTypes.add(codeType);
        if(code.getCodes() != null){
            code.getCodes().stream().forEach(subCode -> {
                CodeType subCodeType = new CodeType();
                subCodeType.setValue(subCode.getValue());
                subCodeType.setLabel(subCode.getLabel());
                subCodeType.setParent(code.getValue());
                codeTypes.addAll(convertFromCodeListDTDToCodeTypeWithParent(subCode, code.getValue()));
            });
        }
        return codeTypes;
    }

    public static CodeList convertFromCodeListDTDtoCodeListModel(CodesList codesListDtd){
        String noParent= "";
        CodeList codeList = new CodeList();
        codeList.setId(codesListDtd.getId());
        codeList.setLabel(codesListDtd.getLabel());
        List<CodeType> codeTypes = new ArrayList<>();
        codesListDtd.getCodes().stream().forEach(code ->
            codeTypes.addAll(convertFromCodeListDTDToCodeTypeWithParent(code, noParent))
        );
        codeList.getCode().addAll(codeTypes);
        return  codeList;
    }




}
