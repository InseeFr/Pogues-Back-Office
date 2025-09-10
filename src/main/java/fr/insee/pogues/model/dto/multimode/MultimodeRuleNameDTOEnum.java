package fr.insee.pogues.model.dto.multimode;

import lombok.Getter;

@Getter
public enum MultimodeRuleNameDTOEnum {
    /** Respondent has moved. */
    IS_MOVED,
    /** Respondent is no longer in this household. */
    IS_SPLIT
}
