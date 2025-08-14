package fr.insee.pogues.model.dto.variables;

import lombok.Getter;

/**
 * <p>A variable datatype can be one of five types:</p>
 * <ul>
 *     <li>BOOLEAN</li>
 *     <li>DATE</li>
 *     <li>DURATION</li>
 *     <li>NUMERIC</li>
 *     <li>TEXT</li>
 * </ul>
 */
@Getter
public enum VariableDTODatatypeTypeEnum {

	/** The variable value is either true or false. */
	BOOLEAN,
	/** The variable can be one of the following format: "YYYY", "YYYY-MM", "YYYY-MM-DD" and can have its value
	 * restricted between a min and max. */
	DATE,
	/** The variable can be one of the following format: "PnYnM" (year and month), "PTnHnM" (minute and second), and can
	 * have its value restricted between a min and max. */
	DURATION,
	/** The variable is a number with a set number of decimals, can have a unit and can have its value restricted
	 * between a min and a max. */
	NUMERIC,
	/** The variable is a string and can have a maximum length (usually 254). */
	TEXT;

}
