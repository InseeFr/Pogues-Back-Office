package fr.insee.pogues.metadata.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Unit {

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("label")
    public String label;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

   

}
