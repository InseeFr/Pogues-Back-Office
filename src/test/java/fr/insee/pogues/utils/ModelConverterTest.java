package fr.insee.pogues.utils;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.webservice.model.dtd.codelists.Code;
import fr.insee.pogues.webservice.model.dtd.codelists.CodesList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.CodesListConverter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelConverterTest {

    @Test
    void testConversionSimpleCodeListToPoguesModel(){
        CodesList codesList = new CodesList("h-f","Homme-Femme", List.of(
                new Code("F","Femme",null),
                new Code("H","Homme",null)
        ));
        fr.insee.pogues.model.CodeList codeListPoguesModel = convertFromCodeListDTDtoCodeListModel(codesList);
        assertEquals("h-f", codeListPoguesModel.getId());
        assertEquals("Homme-Femme", codeListPoguesModel.getLabel());
        assertEquals(2, codeListPoguesModel.getCode().size());
    }

    @Test
    void testConversionHierarchicalCodeListToPoguesModel(){
        CodesList codesList = new CodesList("h-f","Homme-Femme", List.of(
                new Code("F","Femme", List.of(
                        new Code("F1", "Femme 1", null),
                        new Code("F2", "Femme 2", null),
                        new Code("F3", "Femme 3", null)
                )),
                new Code("H","Homme", List.of(
                        new Code("H1", "Homme 1", null),
                        new Code("H2", "Homme 2", null),
                        new Code("H3", "Homme 3", null)
                ))
        ));
        fr.insee.pogues.model.CodeList codeListPoguesModel = convertFromCodeListDTDtoCodeListModel(codesList);
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
    void testConversionPoguesModelCodeListToSimpleCodeList(){
        CodeList poguesModelCodeList = new CodeList();
        poguesModelCodeList.setId("h-f");
        poguesModelCodeList.setLabel("Homme-Femme");
        CodeType codeTypeF = createCodeType("","F","Femme");
        CodeType codeTypeH = createCodeType("","H","Homme");
        poguesModelCodeList.getCode().addAll(List.of(codeTypeF,codeTypeH));
        CodesList codesListDTD = convertFromCodeListModelToCodeListDTD(poguesModelCodeList);
        assertEquals("h-f", codesListDTD.getId());
        assertEquals("Homme-Femme", codesListDTD.getLabel());
        assertEquals(2, codesListDTD.getCodes().size());
    }

    @Test
    void testConversionHierarchicalPoguesModelCodeListToSimpleCodeList(){
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
        CodesList codesListDTD = convertFromCodeListModelToCodeListDTD(poguesModelCodeList);
        assertEquals("h-f", codesListDTD.getId());
        assertEquals("Homme-Femme", codesListDTD.getLabel());
        assertEquals(2, codesListDTD.getCodes().size());
        assertEquals(3, codesListDTD.getCodes().get(0).getCodes().size());
        assertEquals(3, codesListDTD.getCodes().get(1).getCodes().size());
        assertEquals("Femme 2", codesListDTD.getCodes().get(0).getCodes().get(1).getLabel());
    }


}
