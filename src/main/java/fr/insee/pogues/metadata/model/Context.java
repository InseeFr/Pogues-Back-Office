package fr.insee.pogues.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Context {
	
	@Getter @Setter
	@JsonProperty("dataCollectionId")
	String dataCollectionId;
	
	@Getter @Setter
	@JsonProperty("serieId")
	String serieId;
	
	@Getter @Setter
	@JsonProperty("operationId")
	String operationId;

	@Override
	public String toString() {
		return "Context [dataCollectionId=" + dataCollectionId + ", serieId=" + serieId + ", operationId=" + operationId
				+ "]";
	}

}
