package fr.insee.pogues.exception.mapping.codes.list.registre;

import fr.insee.pogues.controller.MappingCodesListRegistreController;
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

@RestControllerAdvice(assignableTypes = MappingCodesListRegistreController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MappingCodesListRegistreControllerAdvice {

    @ExceptionHandler(MappingNotFoundException.class)
    public ProblemDetail handleMappingNotFound(
            MappingNotFoundException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(
                        HttpStatus.NOT_FOUND
                );

        problemDetail.setTitle(
                "Mapping not found"
        );

        problemDetail.setDetail(
                ex.getMessage()
        );

        problemDetail.setInstance(
                URI.create(request.getRequestURI())
        );

        return problemDetail;
    }

    @ExceptionHandler(PoguesCodesListAlreadyMappedException.class)
    public ProblemDetail handlePoguesAlreadyExists(
            PoguesCodesListAlreadyMappedException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Pogues mapping already exists");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    @ExceptionHandler(RegistreCodesListAlreadyMappedException.class)
    public ProblemDetail handleRegistreAlreadyExists(
            RegistreCodesListAlreadyMappedException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problemDetail.setTitle("Registre mapping already exists");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        return problemDetail;
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        ProblemDetail problemDetail =
                ProblemDetail.forStatus(
                        HttpStatus.BAD_REQUEST
                );

        problemDetail.setTitle(
                "Validation failed"
        );

        problemDetail.setDetail(
                "Request body is invalid"
        );

        problemDetail.setInstance(
                URI.create(request.getRequestURI())
        );

        List<String> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                error.getField()
                                        + ": "
                                        + error.getDefaultMessage()
                        )
                        .toList();

        problemDetail.setProperty(
                "errors",
                errors
        );

        return problemDetail;
    }
}
