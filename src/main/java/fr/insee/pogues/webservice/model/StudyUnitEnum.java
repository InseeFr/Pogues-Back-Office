package fr.insee.pogues.webservice.model;

import lombok.Getter;

public enum StudyUnitEnum {
	
	DEFAULT("default"),BUSINESS("business"),HOUSEHOLD("household");
	
	@Getter
	private String studyUnit;

	private StudyUnitEnum(String studyUnit) {
		this.studyUnit = studyUnit;
	}
		

}
