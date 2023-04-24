package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertIterations implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        Questionnaire.Iterations refIterations = referencedQuestionnaire.getIterations();
        if (refIterations != null) {
            questionnaire.setIterations(new Questionnaire.Iterations());
            questionnaire.getIterations().getIteration().addAll(refIterations.getIteration());
            log.info("Iterations from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
        } else {
            log.info("No iterations in referenced questionnaire '{}'", referencedQuestionnaire.getId());
        }
    }

}
