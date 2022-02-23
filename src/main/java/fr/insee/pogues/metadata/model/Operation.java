package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Operation {
	
	@Getter
	@Setter
	@JsonProperty("label")
	List<Label> labels;
	
	@Getter
	@Setter
	@JsonProperty("id")
	String id;
	
	@Getter @Setter
	@JsonProperty("serie")
	Serie serie;

	
	public Operation() {
		super();
	}

	public Operation(List<Label> labels, String id, Serie serie) {
		super();
		this.labels = labels;
		this.id = id;
		this.serie = serie;
	}

	@Override
	public String toString() {
		return "Operation [labels=" + labels + ", id=" + id + ", serie=" + serie + "]";
	}

}
