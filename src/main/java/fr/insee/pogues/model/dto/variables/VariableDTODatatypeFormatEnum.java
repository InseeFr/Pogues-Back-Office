package fr.insee.pogues.model.dto.variables;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum VariableDTODatatypeFormatEnum {

	/** Format to describe a Date with year, month and day. */
	DATE_YEAR_MONTH_DAY("YYYY-MM-DD"),
	/** Format to describe a Date with year and month. */
	DATE_YEAR_MONTH("YYYY-MM"),
	/** Format to describe a Date with year. */
	DATE_YEAR("YYYY"),
	/** Format to describe a Duration datatype with years and months. */
	DURATION_YEAR_MONTH("PnYnM"),
	/** Format to describe a Duration datatype with minutes and seconds. */
	DURATION_MINUTE_SECOND("PTnHnM");

	@JsonValue
	private final String value;

	VariableDTODatatypeFormatEnum(String value) {
		this.value = value;
	}

}
