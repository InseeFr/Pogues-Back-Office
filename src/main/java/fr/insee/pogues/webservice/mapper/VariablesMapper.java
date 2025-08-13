package fr.insee.pogues.webservice.mapper;


import fr.insee.pogues.exception.VariableInvalidModelException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.webservice.model.dto.variables.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

@Component
public class VariablesMapper {

    private VariablesMapper() {}

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
        variable.setName(variableDTO.getName());
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
                if (variableDTODatatype.getDecimals() != null) {
                    datatype.setDecimals(BigInteger.valueOf(variableDTODatatype.getDecimals()));
                }
                datatype.setIsDynamicUnit(variableDTODatatype.getIsDynamicUnit());
                datatype.setUnit(variableDTODatatype.getUnit());
                variable.setDatatype(datatype);
                break;
            }
            case VariableDTODatatypeTypeEnum.TEXT: {
                TextDatatypeType datatype = new TextDatatypeType();
                datatype.setTypeName(DatatypeTypeEnum.TEXT);
                if ((variableDTODatatype.getMaxLength()) != null) {
                    datatype.setMaxLength(BigInteger.valueOf(variableDTODatatype.getMaxLength()));
                }
                variable.setDatatype(datatype);
                break;
            }
            default: {
                throw new VariableInvalidModelException(String.format("Invalid variable datatype %s", variableDTODatatype.getTypeName()), variableDTODatatype.toString());
            }
        }
    }

    private static void setDateDatatypeFormat(DateDatatypeType variable, VariableDTODatatypeFormatEnum variableDTODatatypeFormat) throws VariableInvalidModelException {
        switch (variableDTODatatypeFormat) {
            case VariableDTODatatypeFormatEnum.DATE_YEAR -> variable.setFormat(DateFormatEnum.YYYY);
            case VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH -> variable.setFormat(DateFormatEnum.YYYY_MM);
            case VariableDTODatatypeFormatEnum.DATE_YEAR_MONTH_DAY -> variable.setFormat(DateFormatEnum.YYYY_MM_DD);
            default -> throw new VariableInvalidModelException(String.format("Invalid date format %s", variableDTODatatypeFormat), null);
        }
    }

    private static void setDurationDatatypeFormat(DurationDatatypeType variable, VariableDTODatatypeFormatEnum variableDTODatatypeFormat) throws VariableInvalidModelException {
        switch (variableDTODatatypeFormat) {
            case VariableDTODatatypeFormatEnum.DURATION_MINUTE_SECOND -> variable.setFormat("PTnHnM");
            case VariableDTODatatypeFormatEnum.DURATION_YEAR_MONTH -> variable.setFormat("PnYnM");
            default -> throw new VariableInvalidModelException(String.format("Invalid duration format %s", variableDTODatatypeFormat), null);
        }
    }

    /**
     * @throws VariableInvalidModelException The variable has an invalid model (type or datatype).
     */
    public static VariableDTO toDTO(VariableType variable) throws VariableInvalidModelException {
        VariableDTODatatype datatypeDTO;

        switch (variable.getDatatype().getTypeName()) {
            case DatatypeTypeEnum.BOOLEAN -> datatypeDTO = VariableDTODatatype.booleanDatatype();
            case DatatypeTypeEnum.DATE -> datatypeDTO = VariableDTODatatype.dateDatatype(
                    computeDateDatatypeFormat(((DateDatatypeType) variable.getDatatype()).getFormat()),
                    ((DateDatatypeType) variable.getDatatype()).getMinimum(),
                    ((DateDatatypeType) variable.getDatatype()).getMaximum());
            case DatatypeTypeEnum.DURATION -> datatypeDTO = VariableDTODatatype.durationDatatype(
                    computeDurationDatatypeFormat(((DurationDatatypeType) variable.getDatatype()).getFormat()),
                    ((DurationDatatypeType) variable.getDatatype()).getMinimum(),
                    ((DurationDatatypeType) variable.getDatatype()).getMaximum());
            case DatatypeTypeEnum.NUMERIC -> datatypeDTO = VariableDTODatatype.numericDatatype(
                    ((NumericDatatypeType) variable.getDatatype()).getMinimum().doubleValue(),
                    ((NumericDatatypeType) variable.getDatatype()).getMaximum().doubleValue(),
                    ((NumericDatatypeType) variable.getDatatype()).getDecimals() != null ? ((NumericDatatypeType) variable.getDatatype()).getDecimals().intValue() : null,
                    ((NumericDatatypeType) variable.getDatatype()).isIsDynamicUnit() != null ? ((NumericDatatypeType) variable.getDatatype()).isIsDynamicUnit() : false,
                    ((NumericDatatypeType) variable.getDatatype()).getUnit());
            case DatatypeTypeEnum.TEXT -> datatypeDTO = VariableDTODatatype.textDatatype(
                    ((TextDatatypeType) variable.getDatatype()).getMaxLength() != null ? ((TextDatatypeType) variable.getDatatype()).getMaxLength().intValue() : null);
            default -> throw new VariableInvalidModelException(String.format("Invalid variable datatype %s", variable.getDatatype().getTypeName()), variable.toString());
        }

        switch (variable) {
            case CollectedVariableType v -> {
                return new VariableDTO(v.getId(), v.getName(), v.getLabel(), VariableDTOTypeEnum.COLLECTED, v.getScope(), null, datatypeDTO);
            }
            case ExternalVariableType v -> {
                return new VariableDTO(v.getId(), v.getName(), v.getLabel(), VariableDTOTypeEnum.EXTERNAL, v.getScope(), null, datatypeDTO);
            }
            case CalculatedVariableType v -> {
                return new VariableDTO(v.getId(), v.getName(), v.getLabel(), VariableDTOTypeEnum.CALCULATED, v.getScope(), v.getFormula().getValue(), datatypeDTO);
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
