package fr.insee.pogues.utils;


import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.codelists.Code;
import fr.insee.pogues.webservice.model.dtd.codelists.CodesList;

import java.util.ArrayList;
import java.util.Collection;
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
            code.getCodes().forEach(subCode -> {
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
        List<CodeType> codeTypes = codesListDtd.getCodes().stream()
                .map(code -> convertFromCodeListDTDToCodeTypeWithParent(code, noParent))
                .flatMap(Collection::stream).toList();
        codeList.getCode().addAll(codeTypes);
        return codeList;
    }

    public static CodeType createCodeType(String parent, String id, String label){
        CodeType codeType = new CodeType();
        codeType.setParent(parent);
        codeType.setValue(id);
        codeType.setLabel(label);
        return codeType;
    }

    private static List<Code> getSubCodesByParentCodeValue(List<CodeType> codeTypes, String parentCodeValue){
        List<Code> subCodes = codeTypes.stream()
                .filter(codeType -> parentCodeValue.equals(codeType.getParent()))
                .map(codeType -> new Code(codeType.getValue(), codeType.getLabel(), getSubCodesByParentCodeValue(codeTypes, codeType.getValue())))
                .toList();
        return subCodes.isEmpty() ? null : subCodes;
    }

    private static List<Code> getRootCodesFromCodeList(List<CodeType> codeTypes){
        return codeTypes.stream()
                .filter(codeType -> codeType.getParent() == null || codeType.getParent().isEmpty())
                .map(codeType -> new Code(codeType.getValue(), codeType.getLabel(), getSubCodesByParentCodeValue(codeTypes, codeType.getValue())))
                .toList();
    }

    public static CodesList convertFromCodeListModelToCodeListDTD(CodeList codeList){
        return new CodesList(codeList.getId(), codeList.getLabel(), getRootCodesFromCodeList(codeList.getCode()));
    }




}
