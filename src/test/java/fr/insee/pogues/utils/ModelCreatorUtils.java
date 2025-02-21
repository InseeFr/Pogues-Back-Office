package fr.insee.pogues.utils;

import fr.insee.pogues.model.CodeType;

import java.util.ArrayList;
import java.util.List;

public class ModelCreatorUtils {


    public static CodeType initFakeCodeType(String value, String label, String parent){
        CodeType codeType = new CodeType();
        codeType.setValue(value);
        codeType.setLabel(label);
        codeType.setParent(parent);
        return codeType;
    }

    public static fr.insee.pogues.model.CodeList initFakeCodeList(String id, String label){
        fr.insee.pogues.model.CodeList codeList = new fr.insee.pogues.model.CodeList();
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

    public static List<fr.insee.pogues.model.CodeList> initFakeCodeLists(int size){
        List<fr.insee.pogues.model.CodeList> codeLists = new ArrayList<>();
        for(int i = 0; i < size; i++){
            codeLists.add(initFakeCodeList("code-list-"+i, "Super code list "+i));
        }
        return codeLists;
    }
}
