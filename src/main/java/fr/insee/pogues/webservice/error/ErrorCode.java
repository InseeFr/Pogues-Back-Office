package fr.insee.pogues.webservice.error;

public enum ErrorCode {
    CODE_LIST_RELATED_QUESTIONS_NAME("codelist:relatedquestions:name"),
    CODE_LIST_NOT_FOUND("codelist:notfound"),
    VARIABLE_NOT_FOUND("variable:notfound");

    public final String label;

    ErrorCode(String label) {
        this.label = label;
    }
}
