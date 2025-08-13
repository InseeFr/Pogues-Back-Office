package fr.insee.pogues.webservice.model.dto.variables;

import lombok.Getter;

/**
 * <p>A variable can be one of three types:</p>
 * <ul>
 *     <li>COLLECTED (if it is directly computed from the respondent answers, for example "in which city do you live?")
 *     </li>
 *     <li>EXTERNAL (if it is provided for the respondent, for example "was the respondent interrogated a year ago?"
 *     which can filter specific questions)</li>
 *     <li>CALCULATED (if the value is calculated from a formula, for example "sum of household inhabitants")</li>
 * </ul>
 */
@Getter
public enum VariableDTOTypeEnum {

	/** The variable value is linked to a question. */
	COLLECTED,
	/** The variable value is calculated from a formula. */
	CALCULATED,
	/** The variable value is explicitly provided for the respondent beforehand. */
	EXTERNAL;

}
