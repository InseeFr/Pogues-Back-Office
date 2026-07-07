package fr.insee.pogues.service.stub;

import fr.insee.pogues.model.Questionnaire;
import fr.insee.pogues.service.modelcleaning.ModelCleaner;

public class ModelCleanerServiceStub implements ModelCleaner {
    @Override
    public void apply(Questionnaire questionnaire) {
        // do nothing in this stub
    }
}
