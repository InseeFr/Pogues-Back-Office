package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.CodeList;
import fr.insee.pogues.model.CodeType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodesListTest {

    @Test
    void testGetOnlyCodesWithoutChild() {
        List<CodeType> codes =  List.of(
                initFakeCodeType("H-h","Homme","H"),
                initFakeCodeType("H-f","Femme","H"),
                initFakeCodeType("H","Humain",""),
                initFakeCodeType("M-h","Homme martien","M"),
                initFakeCodeType("M-f","Femme martienne","M"),
                initFakeCodeType("M","Martien martienne",""),
                initFakeCodeType("Z","Seul", "")
        );
        CodeList codeList = new CodeList();
        codeList.getCode().addAll(codes);

        List<CodeType> onlyChild = CodesList.getOnlyCodesWithoutChild(codeList);
        assertEquals(5,onlyChild.size());
        assertTrue(onlyChild.stream().anyMatch(c->c.getLabel().equals("Homme martien")));
        assertTrue(onlyChild.stream().anyMatch(c->c.getLabel().equals("Seul")));
        assertTrue(onlyChild.stream().noneMatch(c->c.getLabel().equals("Humain")));
    }
}
