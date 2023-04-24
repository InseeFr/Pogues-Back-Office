package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;

public class InsertIterations implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        questionnaire.setIterations(new Questionnaire.Iterations());
        questionnaire.getIterations().getIteration().addAll(referencedQuestionnaire.getIterations().getIteration());
    }

}
