package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;

public class InsertCodeLists implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        questionnaire.setCodeLists(new CodeLists());
        questionnaire.getCodeLists().getCodeList().addAll(referencedQuestionnaire.getCodeLists().getCodeList());
    }

}
