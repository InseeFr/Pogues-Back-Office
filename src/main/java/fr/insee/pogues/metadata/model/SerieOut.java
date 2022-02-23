package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SerieOut {
	
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
	String frequence;

	@Override
	public String toString() {
		return "SerieOut [labels=" + labels + ", id=" + id + ", frequence=" + frequence + "]";
	}

}
