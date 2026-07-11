package fr.insee.pogues.exception.validation;

import fr.insee.pogues.controller.QuestionnaireController;
import fr.insee.pogues.controller.VisualizeWithURI;
import fr.insee.pogues.controller.error.ErrorCode;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice(assignableTypes = { QuestionnaireController.class, VisualizeWithURI.class })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ModelValidationControllerAdvice {

    private static final String ERROR_CODE = "errorCode";

    @ExceptionHandler(ModelValidationException.class)
    public ProblemDetail handleModelValidationException(ModelValidationException ex, HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Questionnaire validation failed.");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty("details", ex.getDetails());
        problemDetail.setProperty(ERROR_CODE, ErrorCode.QUESTIONNAIRE_INVALID.label);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    @ExceptionHandler(QuestionnaireIdentifierException.class)
    public ProblemDetail handleQuestionnaireIdentifierException(QuestionnaireIdentifierException ex, HttpServletRequest request) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Questionnaire identifier is invalid.");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setProperty(ERROR_CODE, ErrorCode.QUESTIONNAIRE_IDENTIFIER_INVALID.label);
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }
}
