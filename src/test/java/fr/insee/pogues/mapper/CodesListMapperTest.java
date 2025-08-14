package fr.insee.pogues.mapper;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.model.dto.codeslists.CodeDTO;
import fr.insee.pogues.model.dto.codeslists.CodesListDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.mapper.CodesListMapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodesListMapperTest {

    static CodeType createCodeType(String parent, String id, String label){
        CodeType codeType = new CodeType();
        codeType.setParent(parent);
        codeType.setValue(id);
        codeType.setLabel(label);
        return codeType;
    }

    @Test
    @DisplayName("Should convert DTO code list into model")
    void toModel_success(){
        CodesListDTO codesListDTO = new CodesListDTO("h-f","Homme-Femme", List.of(
                new CodeDTO("F","Femme",null),
                new CodeDTO("H","Homme",null)
        ));
        fr.insee.pogues.model.CodeList codeListPoguesModel = toModel(codesListDTO);
        assertEquals("h-f", codeListPoguesModel.getId());
        assertEquals("Homme-Femme", codeListPoguesModel.getLabel());
        assertEquals(2, codeListPoguesModel.getCode().size());
    }

    @Test
    @DisplayName("Should convert DTO code list with hierarchy into model")
    void toModel_success_hierarchicalCodeList(){
        CodesListDTO codesListDTO = new CodesListDTO("h-f","Homme-Femme", List.of(
                new CodeDTO("F","Femme", List.of(
                        new CodeDTO("F1", "Femme 1", null),
                        new CodeDTO("F2", "Femme 2", null),
                        new CodeDTO("F3", "Femme 3", null)
                )),
                new CodeDTO("H","Homme", List.of(
                        new CodeDTO("H1", "Homme 1", null),
                        new CodeDTO("H2", "Homme 2", null),
                        new CodeDTO("H3", "Homme 3", null)
                ))
        ));
        fr.insee.pogues.model.CodeList codeListPoguesModel = toModel(codesListDTO);
        assertEquals("h-f", codeListPoguesModel.getId());
        assertEquals("Homme-Femme", codeListPoguesModel.getLabel());
        assertEquals(8, codeListPoguesModel.getCode().size());
        CodeType codeType5th = codeListPoguesModel.getCode().get(5);
        assertEquals("H1", codeType5th.getValue());
        assertEquals("Homme 1", codeType5th.getLabel());
        assertEquals("H", codeType5th.getParent());
        CodeType codeType4th = codeListPoguesModel.getCode().get(4);
        assertEquals("", codeType4th.getParent());
    }

    @Test
    @DisplayName("Should convert model code list into DTO")
    void toDTO_success() {
        CodeList poguesModelCodeList = new CodeList();
        poguesModelCodeList.setId("h-f");
        poguesModelCodeList.setLabel("Homme-Femme");
        CodeType codeTypeF = createCodeType("","F","Femme");
        CodeType codeTypeH = createCodeType("","H","Homme");
        poguesModelCodeList.getCode().addAll(List.of(codeTypeF,codeTypeH));
        CodesListDTO codesListDTO = toDTO(poguesModelCodeList);
        assertEquals("h-f", codesListDTO.getId());
        assertEquals("Homme-Femme", codesListDTO.getLabel());
        assertEquals(2, codesListDTO.getCodes().size());
    }

    @Test
    @DisplayName("Should convert model code list with hierarchy into DTO")
    void toDTO_success_hierarchicalCodeList() {
        CodeList poguesModelCodeList = new CodeList();
        poguesModelCodeList.setId("h-f");
        poguesModelCodeList.setLabel("Homme-Femme");
        CodeType codeTypeF = createCodeType("","F","Femme");
        CodeType codeTypeF1 = createCodeType("F","F1","Femme 1");
        CodeType codeTypeF2 = createCodeType("F","F2","Femme 2");
        CodeType codeTypeF3 = createCodeType("F","F3","Femme 3");
        CodeType codeTypeH = createCodeType("","H","Homme");
        CodeType codeTypeH1 = createCodeType("H","H1","Homme 1");
        CodeType codeTypeH2 = createCodeType("H","H2","Homme 2");
        CodeType codeTypeH3 = createCodeType("H","H3","Homme 3");
        poguesModelCodeList.getCode().addAll(List.of(
                codeTypeF,codeTypeF1,codeTypeF2,codeTypeF3,
                codeTypeH,codeTypeH1,codeTypeH2,codeTypeH3));
        CodesListDTO codesListDTO = toDTO(poguesModelCodeList);
        assertEquals("h-f", codesListDTO.getId());
        assertEquals("Homme-Femme", codesListDTO.getLabel());
        assertEquals(2, codesListDTO.getCodes().size());
        assertEquals(3, codesListDTO.getCodes().get(0).getCodes().size());
        assertEquals(3, codesListDTO.getCodes().get(1).getCodes().size());
        assertEquals("Femme 2", codesListDTO.getCodes().get(0).getCodes().get(1).getLabel());
    }


}
