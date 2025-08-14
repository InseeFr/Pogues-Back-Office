package fr.insee.pogues.model;

import lombok.Getter;

public enum CaptureEnum {
	
	OPTICAL("optical"), MANUAL("manual");
	
	@Getter
	private String capture;

	private CaptureEnum(String capture) {
		this.capture = capture;
	}
	
	

}
