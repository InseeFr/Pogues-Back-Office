package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of CompositionStep to insert code lists of a referenced questionnaire.
 */
@Slf4j
class InsertCodeLists implements CompositionStep {

    /**
     * Insert code lists of the referenced questionnaire in the referencing questionnaire.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
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
