package fr.insee.pogues.model.dto.multimode;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Multimode can be set to switch how we collect data from the respondent (e.g. from web to one-on-one).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultimodeDTO {
    /** Rules that apply to the global variables of a questionnaire. */
    private MultimodeItemDTO questionnaire;
    /** Rules that apply to the variables of a roundabout. */
    private MultimodeItemDTO leaf;
}
