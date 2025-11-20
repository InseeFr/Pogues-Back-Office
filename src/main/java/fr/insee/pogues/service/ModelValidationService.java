package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesValidationException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.validation.MandatoryCodeListMCQCheck;
import fr.insee.pogues.service.validation.ValidationResult;
import fr.insee.pogues.service.validation.ValidationStep;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelValidationService {

    /**
     * Checks if there is any issue in the questionnaire (e.g. an invalid question property).
     * @throws PoguesValidationException if there is.
     */
    public void validate(Questionnaire questionnaire) throws PoguesValidationException {
        List<ValidationStep> validationSteps = List.of(
                new MandatoryCodeListMCQCheck()
        );
        List<String> errors = validationSteps.stream()
                .map(validationStep -> validationStep.validate(questionnaire))
                .filter(validationResult -> !validationResult.isValid())
                .map(ValidationResult::errorMessage)
                .toList();
        int errorsCount = errors.size();
        if (errorsCount > 0)
            throw new PoguesValidationException(errors.getFirst() +
                    " (%d other error%s)".formatted(errorsCount - 1, (errorsCount - 1) > 1));
    }

}
