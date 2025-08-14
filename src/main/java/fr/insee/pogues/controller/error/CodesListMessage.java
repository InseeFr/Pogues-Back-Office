package fr.insee.pogues.controller.error;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodesListMessage extends ApiMessage {

    private final List<String> relatedQuestionNames;

    public CodesListMessage(int status, String message, String details, List<String> relatedQuestionNames, ErrorCode errorCode) {
        super(status, message, details, errorCode.label);
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
