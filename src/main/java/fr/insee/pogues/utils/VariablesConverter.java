package fr.insee.pogues.utils;


import fr.insee.pogues.exception.VariableInvalidModelException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dtd.variables.*;

import java.math.BigDecimal;
import java.math.BigInteger;

public class VariablesConverter {

    private VariablesConverter() {}

    /**
     * @throws VariableInvalidModelException The variable has an invalid model (type or datatype).
     */
    public static VariableType toModel(VariableDTO variableDTO) throws VariableInvalidModelException {
        VariableType variable;

        switch (variableDTO.getType()) {
            case VariableDTOTypeEnum.COLLECTED -> variable = new CollectedVariableType();
            case VariableDTOTypeEnum.EXTERNAL -> variable = new ExternalVariableType();
            case VariableDTOTypeEnum.CALCULATED -> {
                variable = new CalculatedVariableType();
                ExpressionType formula = new ExpressionType();
                formula.setValue(variableDTO.getFormula());
                ((CalculatedVariableType) variable).setFormula(formula);
            }
            default -> throw new VariableInvalidModelException(String.format("Invalid variable type %s", variableDTO.getType()), variableDTO.toString());
        }

        variable.setId(variableDTO.getId());
        variable.setName(variableDTO.getLabel());
        variable.setLabel(variableDTO.getDescription());
        variable.setScope(variableDTO.getScope());

        setDatatype(variable, variableDTO.getDatatype());

        return variable;
    }

    /**
     * @throws VariableInvalidModelException The variable has an invalid datatype.
     */
    private static void setDatatype(VariableType variable, VariableDTODatatype variableDTODatatype) throws VariableInvalidModelException {
        switch (variableDTODatatype.getTypeName()) {
            case VariableDTODatatypeTypeEnum.BOOLEAN: {
                BooleanDatatypeType datatype = new BooleanDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
                variable.setDatatype(datatype);
                break;
            }
            case VariableDTODatatypeTypeEnum.DATE: {
                DateDatatypeType datatype = new DateDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.DATE);
                setDateDatatypeFormat(datatype, variableDTODatatype.getFormat());
                datatype.setMinimum((String) variableDTODatatype.getMinimum());
                datatype.setMaximum((String) variableDTODatatype.getMaximum());
                variable.setDatatype(datatype);
                break;
            }
            case VariableDTODatatypeTypeEnum.DURATION: {
                DurationDatatypeType datatype = new DurationDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.DURATION);
                setDurationDatatypeFormat(datatype, variableDTODatatype.getFormat());
                datatype.setMinimum((String) variableDTODatatype.getMinimum());
                datatype.setMaximum((String) variableDTODatatype.getMaximum());
                variable.setDatatype(datatype);
                break;
            }
            case VariableDTODatatypeTypeEnum.NUMERIC: {
                NumericDatatypeType datatype = new NumericDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.NUMERIC);
                datatype.setMinimum(new BigDecimal(variableDTODatatype.getMinimum().toString()));
                datatype.setMaximum(new BigDecimal(variableDTODatatype.getMaximum().toString()));
                datatype.setDecimals(BigInteger.valueOf(variableDTODatatype.getDecimals()));
                datatype.setIsDynamicUnit(variableDTODatatype.getIsDynamicUnit());
                datatype.setUnit(variableDTODatatype.getUnit());
                variable.setDatatype(datatype);
                break;
            }
            case VariableDTODatatypeTypeEnum.TEXT: {
                TextDatatypeType datatype = new TextDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.TEXT);
                datatype.setMaxLength(BigInteger.valueOf(variableDTODatatype.getMaxLength()));
                variable.setDatatype(datatype);
                break;
            }
            default: {
                throw new VariableInvalidModelException(String.format("Invalid variable datatype %s", variableDTODatatype.getTypeName()), variableDTODatatype.toString());
            }
        }
    }

    private static void setDateDatatypeFormat(DateDatatypeType variable, VariableDTODatatypeFormatEnum variableDTODatatypeFormat) {
        switch (variableDTODatatypeFormat) {
            case VariableDTODatatypeFormatEnum.DATE_YEAR -> variable.setFormat(DateFormatEnum.YYYY);
            case VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH -> variable.setFormat(DateFormatEnum.YYYY_MM);
            case VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH_DAY -> variable.setFormat(DateFormatEnum.YYYY_MM_DD);
        }
    }

    private static void setDurationDatatypeFormat(DurationDatatypeType variable, VariableDTODatatypeFormatEnum variableDTODatatypeFormat) {
        switch (variableDTODatatypeFormat) {
            case VariableDTODatatypeFormatEnum.DURATION_MINUTE_SECOND -> variable.setFormat("PTnHnM");
            case VariableDTODatatypeFormatEnum.DURATION_YEAR_MONTH -> variable.setFormat("PnYnM");
        }
    }

    /**
     * @throws VariableInvalidModelException The variable has an invalid model (type or datatype).
     */
    public static VariableDTO toDTO(VariableType variable) throws VariableInvalidModelException {
        VariableDTODatatype datatypeDTO;

        switch (variable.getDatatype().getTypeName()) {
            case DatatypeTypeEnum.BOOLEAN -> datatypeDTO = new VariableDTODatatype(
                    VariableDTODatatypeTypeEnum.BOOLEAN,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            case DatatypeTypeEnum.DATE -> datatypeDTO = new VariableDTODatatype(
                    VariableDTODatatypeTypeEnum.DATE,
                    computeDateDatatypeFormat(((DateDatatypeType) variable.getDatatype()).getFormat()),
                    ((DateDatatypeType) variable.getDatatype()).getMinimum(),
                    ((DateDatatypeType) variable.getDatatype()).getMaximum(),
                    null,
                    null,
                    null,
                    null);
            case DatatypeTypeEnum.DURATION -> datatypeDTO = new VariableDTODatatype(
                    VariableDTODatatypeTypeEnum.DURATION,
                    computeDurationDatatypeFormat(((DurationDatatypeType) variable.getDatatype()).getFormat()),
                    ((DurationDatatypeType) variable.getDatatype()).getMinimum(),
                    ((DurationDatatypeType) variable.getDatatype()).getMaximum(),
                    null,
                    null,
                    null,
                    null);
            case DatatypeTypeEnum.NUMERIC -> datatypeDTO = new VariableDTODatatype(
                    VariableDTODatatypeTypeEnum.NUMERIC,
                    null,
                    ((NumericDatatypeType) variable.getDatatype()).getMinimum().doubleValue(),
                    ((NumericDatatypeType) variable.getDatatype()).getMaximum().doubleValue(),
                    ((NumericDatatypeType) variable.getDatatype()).getDecimals().intValue(),
                    ((NumericDatatypeType) variable.getDatatype()).isIsDynamicUnit(),
                    ((NumericDatatypeType) variable.getDatatype()).getUnit(),
                    null);
            case DatatypeTypeEnum.TEXT -> datatypeDTO = new VariableDTODatatype(
                    VariableDTODatatypeTypeEnum.TEXT,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    ((TextDatatypeType) variable.getDatatype()).getMaxLength().intValue());
            default -> throw new VariableInvalidModelException(String.format("Invalid variable datatype %s", variable.getDatatype().getTypeName()), variable.toString());
        }

        switch (variable) {
            case CollectedVariableType var -> {
                return new VariableDTO(var.getId(), var.getName(), var.getLabel(), VariableDTOTypeEnum.COLLECTED, var.getScope(), null, datatypeDTO);
            }
            case ExternalVariableType var -> {
                return new VariableDTO(var.getId(), var.getName(), var.getLabel(), VariableDTOTypeEnum.EXTERNAL, var.getScope(), null, datatypeDTO);
            }
            case CalculatedVariableType var -> {
                return new VariableDTO(var.getId(), var.getName(), var.getLabel(), VariableDTOTypeEnum.CALCULATED, var.getScope(), var.getFormula().getValue(), datatypeDTO);
            }
            default -> throw new VariableInvalidModelException(String.format("Invalid variable type %s", variable.getClass()), variable.toString());
        }
    }

    private static VariableDTODatatypeFormatEnum computeDateDatatypeFormat(DateFormatEnum format) {
        return switch (format) {
            case DateFormatEnum.YYYY -> VariableDTODatatypeFormatEnum.DATE_YEAR;
            case DateFormatEnum.YYYY_MM -> VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH;
            case DateFormatEnum.YYYY_MM_DD -> VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH_DAY;
        };
    }

    /**
     * @throws VariableInvalidModelException The variable has an invalid duration format.
     */
    private static VariableDTODatatypeFormatEnum computeDurationDatatypeFormat(String format) throws VariableInvalidModelException {
        return switch (format) {
            case "PTnHnM" -> VariableDTODatatypeFormatEnum.DURATION_MINUTE_SECOND;
            case "PnYnM" -> VariableDTODatatypeFormatEnum.DURATION_YEAR_MONTH;
            default ->
                    throw new VariableInvalidModelException(String.format("Invalid duration format %s", format), null);
        };
    }

}
