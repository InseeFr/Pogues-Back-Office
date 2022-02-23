package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Frequence {
	
	@Getter
	@Setter
	@JsonProperty("id")
	String id;
	
	@Getter
	@Setter
	@JsonProperty("label")
	List<Label> labels;
	
	@Getter
	@Setter
	@JsonProperty("uri")
	String uri;

	@Override
	public String toString() {
		return "Frequence [id=" + id + ", labels=" + labels + ", uri=" + uri + "]";
	}
	
}
