package fr.insee.pogues.model.dto.multimode;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultimodeRuleDTO {
    private MultimodeRuleNameDTOEnum name;
    /** VTL formula that will trigger the rule when true. */
    private String value;
}
