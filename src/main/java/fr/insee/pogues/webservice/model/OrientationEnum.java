package fr.insee.pogues.webservice.model;

import lombok.Getter;

public enum OrientationEnum {
	
	ZERO("0"),NINETY("90");
	
	@Getter
	private String orientation;

	private OrientationEnum(String orientation) {
		this.orientation = orientation;
	}	

}
