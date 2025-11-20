package fr.insee.pogues.controller.error;

import fr.insee.pogues.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { GenericException.class, })
    public ResponseEntity<ApiMessage> handleGenericException(GenericException exception) {
        log.error(exception.getMessage(), exception);
        ApiMessage message = exception.toApiMessage();
        return new ResponseEntity<>(message, HttpStatusCode.valueOf(message.getStatus()));
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ApiError> handleOtherException(Exception e) {
        log.error(e.getMessage(), e);
        String cause = e.getCause() != null ? e.getCause().getClass().getSimpleName() : e.getClass().getSimpleName();
        String causeMessage = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
        ApiError apiErrorResponse = new ApiError(500, cause, causeMessage);
        return new ResponseEntity<>(apiErrorResponse, HttpStatus.valueOf(500));
    }

    @ExceptionHandler(PoguesValidationException.class)
    public ResponseEntity<ApiMessage> handleModelValidationException(PoguesValidationException validationException) {
        log.error(validationException.getMessage());
        int httpStatusCode = 400;
        ApiMessage apiMessage = new ApiMessage(
                httpStatusCode,
                "Questionnaire validation failed.",
                validationException.getMessage(),
                ErrorCode.QUESTIONNAIRE_INVALID.label);
        return new ResponseEntity<>(apiMessage, HttpStatus.valueOf(httpStatusCode));
    }

    @ExceptionHandler(PoguesDeserializationException.class)
    public ResponseEntity<ApiMessage> handleDeserializationException(PoguesDeserializationException deserializationException) {
        log.error(deserializationException.getMessage());
        int httpStatusCode = 400;
        ApiMessage apiMessage = new ApiMessage(
                httpStatusCode,
                "Error when de-serializing Pogues questionnaire.",
                deserializationException.getMessage(),
                ErrorCode.QUESTIONNAIRE_INVALID.label);
        return new ResponseEntity<>(apiMessage, HttpStatus.valueOf(httpStatusCode));
    }

    @ExceptionHandler(PoguesSerializationException.class)
    public ResponseEntity<String> handleSerializationException(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                "Error when serializing Pogues questionnaire.", HttpStatus.valueOf(500));
    }

    @ExceptionHandler(QuestionnaireIdentifierException.class)
    public ResponseEntity<ApiMessage> handleInvalidIdentifierException(QuestionnaireIdentifierException exception) {
        log.error(exception.getMessage());
        int httpStatusCode = 400;
        ApiMessage apiMessage = new ApiMessage(
                httpStatusCode,
                "Questionnaire identifier is invalid.",
                exception.getMessage(),
                ErrorCode.QUESTIONNAIRE_IDENTIFIER_INVALID.label);
        return new ResponseEntity<>(apiMessage, HttpStatus.valueOf(httpStatusCode));
    }

}
