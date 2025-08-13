package fr.insee.pogues.model.dto.variables;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datatypes depict the variable format.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariableDTODatatype {

    private VariableDTODatatypeTypeEnum typeName;

    // Date and duration value
    private VariableDTODatatypeFormatEnum format;

    // date string if Date, string with hours/minutes or years/months if Duration, double if Numeric
    private Object minimum;
    private Object maximum;

    // Numeric values
    private Integer decimals;
    private Boolean isDynamicUnit;
    private String unit;

    // Text value
    private Integer maxLength;

    public static VariableDTODatatype booleanDatatype() {
        return new VariableDTODatatype(
                VariableDTODatatypeTypeEnum.BOOLEAN,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static VariableDTODatatype dateDatatype(VariableDTODatatypeFormatEnum format, String minimum, String maximum) {
        return new VariableDTODatatype(
                VariableDTODatatypeTypeEnum.DATE,
                format,
                minimum,
                maximum,
                null,
                null,
                null,
                null);
    }

    public static VariableDTODatatype durationDatatype(VariableDTODatatypeFormatEnum format, String minimum, String maximum) {
        return new VariableDTODatatype(
                VariableDTODatatypeTypeEnum.DURATION,
                format,
                minimum,
                maximum,
                null,
                null,
                null,
                null);
    }

    public static VariableDTODatatype numericDatatype(double minimum, double maximum, Integer decimals, boolean isDynamicUnit, String unit) {
        return new VariableDTODatatype(
                VariableDTODatatypeTypeEnum.NUMERIC,
                null,
                minimum,
                maximum,
                decimals,
                isDynamicUnit,
                unit,
                null);
    }

    public static VariableDTODatatype textDatatype(Integer maxLength) {
        return new VariableDTODatatype(
                VariableDTODatatypeTypeEnum.TEXT,
                null,
                null,
                null,
                null,
                null,
                null,
                maxLength);
    }
}
