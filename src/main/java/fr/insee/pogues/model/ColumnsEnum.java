package fr.insee.pogues.model;

import lombok.Getter;

public enum ColumnsEnum {
	
	ONE("1"),TWO("2"),THREE("3"),FOUR("4");
	
	@Getter
	private String nbcolumn;

	private ColumnsEnum(String nbcolumn) {
		this.nbcolumn = nbcolumn;
	}
	
	
}
