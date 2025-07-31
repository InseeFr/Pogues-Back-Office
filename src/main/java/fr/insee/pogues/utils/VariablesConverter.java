package fr.insee.pogues.utils;


import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.variables.Variable;
import fr.insee.pogues.webservice.model.dtd.variables.VariableTypeEnum;

public class VariablesConverter {

    private VariablesConverter() {}

    public static VariableType convertFromDTDtoModel(Variable variableDTD) throws Exception {
        VariableType variable;
        if (VariableTypeEnum.COLLECTED == variableDTD.getType()) {
            variable = new CollectedVariableType();
        } else if (VariableTypeEnum.EXTERNAL == variableDTD.getType()) {
            variable = new ExternalVariableType();
        } else if (VariableTypeEnum.CALCULATED == variableDTD.getType()) {
            variable = new CalculatedVariableType();
            ExpressionType formula = new ExpressionType();
            formula.setValue(variableDTD.getFormula());
            ((CalculatedVariableType) variable).setFormula(formula);
        } else {
            throw new Exception("Invalid variable type");
        }
        variable.setId(variableDTD.getId());
        variable.setName(variableDTD.getLabel());
        variable.setLabel(variableDTD.getDescription());
        variable.setScope(variableDTD.getScope());
        return variable;
    }

    public static Variable convertFromModelToDTD(VariableType variable) throws Exception {
        if (CollectedVariableType.class == variable.getClass()) {
            return new Variable(variable.getId(), variable.getName(), variable.getLabel(), VariableTypeEnum.COLLECTED, variable.getScope(), "");
        }
        if (ExternalVariableType.class == variable.getClass()) {
            return new Variable(variable.getId(), variable.getName(), variable.getLabel(), VariableTypeEnum.EXTERNAL, variable.getScope(), "");
        }
        if (CalculatedVariableType.class == variable.getClass()) {
            return new Variable(variable.getId(), variable.getName(), variable.getLabel(), VariableTypeEnum.CALCULATED, variable.getScope(), ((CalculatedVariableType) variable).getFormula().getValue());
        }
        throw new Exception("Invalid variable type");
    }
}
