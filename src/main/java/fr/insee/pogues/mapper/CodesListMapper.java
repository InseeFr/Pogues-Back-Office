package fr.insee.pogues.mapper;


import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.codeslists.CodeDTO;
import fr.insee.pogues.model.dto.codeslists.CodesListDTO;
import fr.insee.pogues.model.dto.nomenclatures.ExternalLinkDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodesListMapper {

    private CodesListMapper() {}

    /**
     * Compute the code list into its Pogues model
     * @param codesListDto Code list to convert
     */
    public static CodeList toModel(CodesListDTO codesListDto){
        String noParent= "";
        CodeList codeList = new CodeList();
        codeList.setId(codesListDto.getId());
        codeList.setLabel(codesListDto.getLabel());
        List<CodeType> codeTypes = codesListDto.getCodes().stream()
                .map(code -> convertFromCodeListDTOToCodeTypeWithParent(code, noParent))
                .flatMap(Collection::stream).toList();
        codeList.getCode().addAll(codeTypes);
        return codeList;
    }

    private static List<CodeType> convertFromCodeListDTOToCodeTypeWithParent(CodeDTO codeDTO, String parent){
        List<CodeType> codeTypes = new ArrayList<>();
        CodeType codeType = new CodeType();
        codeType.setValue(codeDTO.getValue());
        codeType.setLabel(codeDTO.getLabel());
        codeType.setParent(parent);
        codeTypes.add(codeType);
        if(codeDTO.getCodes() != null){
            codeDTO.getCodes().forEach(subCode -> {
                CodeType subCodeType = new CodeType();
                subCodeType.setValue(subCode.getValue());
                subCodeType.setLabel(subCode.getLabel());
                subCodeType.setParent(codeDTO.getValue());
                codeTypes.addAll(convertFromCodeListDTOToCodeTypeWithParent(subCode, codeDTO.getValue()));
            });
        }
        return codeTypes;
    }

    /**
     * Compute the code list into its DTO
     * @param codeList Code list to convert
     */
    public static CodesListDTO toDTO(CodeList codeList) {
        return new CodesListDTO(codeList.getId(), codeList.getLabel(), getRootCodesFromCodeList(codeList.getCode()));
    }

    private static List<CodeDTO> getRootCodesFromCodeList(List<CodeType> codeTypes){
        return codeTypes.stream()
                .filter(codeType -> codeType.getParent() == null || codeType.getParent().isEmpty())
                .map(codeType -> new CodeDTO(codeType.getValue(), codeType.getLabel(), getSubCodesByParentCodeValue(codeTypes, codeType.getValue())))
                .toList();
    }

    private static List<CodeDTO> getSubCodesByParentCodeValue(List<CodeType> codeTypes, String parentCodeValue){
        List<CodeDTO> subCodeDTOS = codeTypes.stream()
                .filter(codeType -> parentCodeValue.equals(codeType.getParent()))
                .map(codeType -> new CodeDTO(codeType.getValue(), codeType.getLabel(), getSubCodesByParentCodeValue(codeTypes, codeType.getValue())))
                .toList();
        return subCodeDTOS.isEmpty() ? null : subCodeDTOS;
    }

    /**
     * Compute the code list into its nomenclature DTO
     * @param codeList Nomenclature to convert
     */
    public static NomenclatureDTO toNomenclatureDTO(CodeList codeList){
        ExternalLinkDTO externalLinkDTO = new ExternalLinkDTO(codeList.getUrn());
        // TODO: fix me, use version instead of Name of codeList !!!
        // id, label, version, externalLink
        return new NomenclatureDTO(codeList.getId(), codeList.getLabel(), codeList.getName(), externalLinkDTO);
    }

}
