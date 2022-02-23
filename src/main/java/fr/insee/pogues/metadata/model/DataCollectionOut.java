package fr.insee.pogues.metadata.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class DataCollectionOut {
	
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
	
	public DataCollectionOut() {
		super();
	}

	public DataCollectionOut(List<Label> labels, String id, String parent) {
		super();
		this.labels = labels;
		this.id = id;
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "OperationOut [labels=" + labels + ", id=" + id + ", parent=" + parent + "]";
	}
	

}
