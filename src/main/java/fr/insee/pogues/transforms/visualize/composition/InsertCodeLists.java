package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.exception.DeReferencingException;
import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsertCodeLists implements CompositionStep {

    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire)
            throws DeReferencingException {
        CodeLists refCodeLists = referencedQuestionnaire.getCodeLists();
        if (refCodeLists != null) {
            questionnaire.setCodeLists(new CodeLists());
            questionnaire.getCodeLists().getCodeList().addAll(refCodeLists.getCodeList());
            log.info("Code lists from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
        } else {
            log.info("No code lists in referenced questionnaire '{}'", referencedQuestionnaire.getId());
        }

    }

}
