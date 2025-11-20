package fr.insee.pogues.controller.error;

public enum ErrorCode {
    CODE_LIST_RELATED_QUESTIONS_NAME("codelist:relatedquestions:name"),
    CODE_LIST_NOT_FOUND("codelist:notfound"),
    VARIABLE_NOT_FOUND("variable:notfound"),
    VARIABLE_INVALID_MODEL("variable:invalidmodel"),
    QUESTIONNAIRE_NOT_FOUND("questionnaire:notfound"),
    QUESTIONNAIRE_INVALID("questionnaire:invalid"),
    QUESTIONNAIRE_IDENTIFIER_INVALID("questionnaire:invalididentifier"),
    VERSION_NOT_FOUND("version:notfound"),
    QUESTIONNAIRE_FORMULA_LANGUAGE_NOT_VTL("questionnaire:formulalanguage:notvtl"),
    QUESTIONNAIRE_ROUNDABOUT_NOT_FOUND("questionnaire:roundaboutnotfound"),
    ;

    public final String label;

    ErrorCode(String label) {
        this.label = label;
    }
}
