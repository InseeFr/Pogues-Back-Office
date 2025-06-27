package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.Questionnaire;

public class LoopMinMaxCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        if (questionnaire.getIterations() == null)
            return;
        questionnaire.getIterations().getIteration().stream()
                // the min/max props are on the DynamicIterationType subclass
                .filter(DynamicIterationType.class::isInstance).map(DynamicIterationType.class::cast)
                .forEach(loop -> {
                    loop.setMinimum(loop.getDeprecatedMinimum());
                    loop.setMaximum(loop.getDeprecatedMaximum());
                    loop.setDeprecatedMinimum(null);
                    loop.setDeprecatedMaximum(null);
                });
    }

}
