package fr.insee.pogues.webservice.model.dtd.variables;


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

}
