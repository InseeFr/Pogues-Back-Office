package fr.insee.pogues.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

public class Label {

	@Getter
	@Setter
	@JsonProperty("langue")
	String language;

	@Getter
	@Setter
	@JsonProperty("contenu")
	String content;

	public Label() {
		super();
	}

	public Label(String language, String content) {
		super();
		this.language = language;
		this.content = content;
	}

	@Override
	public String toString() {
		return "Label [language=" + language + ", content=" + content + "]";
	}

}
