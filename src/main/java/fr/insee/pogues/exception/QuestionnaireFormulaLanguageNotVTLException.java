package fr.insee.pogues.exception;

import static fr.insee.pogues.controller.error.ErrorCode.QUESTIONNAIRE_FORMULA_LANGUAGE_NOT_VTL;

/**
 * Exception that is thrown when one tries to use a feature only available to questionnaire whose formula language is VTL.
 */
public class QuestionnaireFormulaLanguageNotVTLException extends PoguesException {

    public QuestionnaireFormulaLanguageNotVTLException(String message) {
        super(422, message, null, QUESTIONNAIRE_FORMULA_LANGUAGE_NOT_VTL);
    }

}
