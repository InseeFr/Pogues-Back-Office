package fr.insee.pogues.utils;

import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.webservice.model.dtd.codeList.Code;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.CodesListConverter.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ModelConverterTest {

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
    void testConversionHierachicalCodeListToPoguesModel(){
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


}
