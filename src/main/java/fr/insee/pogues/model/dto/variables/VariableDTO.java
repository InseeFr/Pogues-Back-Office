package fr.insee.pogues.model.dto.variables;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Variables depict the data computed from the survey.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VariableDTO {

    private String id;
    private String name;
    private String description;
    private VariableDTOTypeEnum type;
    private String scope;
    private String formula;
    private VariableDTODatatype datatype;

}
