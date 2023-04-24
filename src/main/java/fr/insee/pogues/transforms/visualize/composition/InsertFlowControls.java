package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertFlowControls implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        questionnaire.getFlowControl().addAll(referencedQuestionnaire.getFlowControl());
        log.info("FlowControl from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
