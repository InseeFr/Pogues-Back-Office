package fr.insee.pogues.webservice.error;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodesListMessage extends ApiMessage {

    private final List<String> questionsIds;

    public CodesListMessage(int status, String message, String details, List<String> questionsIds) {
        super(status, message, details);
        this.questionsIds = questionsIds;
    }
}
