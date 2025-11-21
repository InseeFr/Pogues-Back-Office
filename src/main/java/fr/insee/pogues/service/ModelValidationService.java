package fr.insee.pogues.service;

import fr.insee.pogues.exception.PoguesValidationException;
import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.validation.MandatoryCodeListMCQCheck;
import fr.insee.pogues.service.validation.ValidationResult;
import fr.insee.pogues.service.validation.ValidationStep;
import fr.insee.pogues.transforms.visualize.ModelTransformer;
import fr.insee.pogues.utils.PoguesDeserializer;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/** Note: implements ModelTransformer to be called in 'visualize' endpoints. */
@Service
public class ModelValidationService implements ModelTransformer {

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

    @Override
    public ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName)
            throws Exception {
        // store input stream content since deserializing consumes the input stream
        byte[] content = inputStream.readAllBytes();
        Questionnaire poguesQuestionnaire = PoguesDeserializer.deserialize(new ByteArrayInputStream(content));
        validate(poguesQuestionnaire);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(content);
        return outputStream;
    }

}
