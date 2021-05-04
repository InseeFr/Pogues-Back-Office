package fr.insee.pogues.utils.vtl;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BindingsUtils {

    public static Bindings createBindingsFromVariables(List<Variable> variables){
        Map<String, Object> variablesMap = variables
                .stream()
                .collect(Collectors.toMap(Variable::getName, variable -> getExampleValue(variable)));
        return new SimpleBindings(variablesMap);
    }

    public static Object getExampleValue(Variable variable){
        // typeName in variable (TEXT/DATE/BOOLEAN/NUMERIC)
        String type = variable.getType();
        switch (type){
            case "TEXT":
                return "toto";
            case "DATE":
                return "12/12/2021";
            case "BOOLEAN":
                return true;
            case "NUMERIC":
                return 42;
            default:
                return "toto";
        }
    }
}
