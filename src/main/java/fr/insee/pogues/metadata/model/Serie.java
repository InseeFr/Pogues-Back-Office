package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Serie {
	
	@Getter
	@Setter
	@JsonProperty("label")
	List<Label> labels;
	
	@Getter
	@Setter
	@JsonProperty("id")
	String id;
	
	@Getter
	@Setter
	@JsonProperty("frequence")
	Frequence frequence;
	
	@Getter @Setter
	@JsonProperty("nbOperation")
	String nbOperation;

	@Override
	public String toString() {
		return "Serie [labels=" + labels + ", id=" + id + ", frequences=" + frequence + ", nbOperation=" + nbOperation
				+ "]";
	}
}
