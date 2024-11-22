package fr.insee.pogues.webservice.model;

import lombok.Getter;

/**
 * Old vocabulary for the "context" concept.
 * @see EnoContext
 * @deprecated Use <code>EnoContext</code> instead.
 */
@Deprecated(since = "4.9.2")
@Getter
public enum StudyUnitEnum {
	
	DEFAULT("default"),BUSINESS("business"),HOUSEHOLD("household");
	
	@Getter
	private final String studyUnit;

	StudyUnitEnum(String studyUnit) {
		this.studyUnit = studyUnit;
	}
		

}
