package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class OperationOut {
	
	@Getter
	@Setter
	@JsonProperty("label")
	List<Label> labels;
	
	@Getter
	@Setter
	@JsonProperty("id")
	String id;
	
	@Getter @Setter
	@JsonProperty("parent")
	String parent;

	@Override
	public String toString() {
		return "OperationOut [labels=" + labels + ", id=" + id + ", parent=" + parent + "]";
	}

}
