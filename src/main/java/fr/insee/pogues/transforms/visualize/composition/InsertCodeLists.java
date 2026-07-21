package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.CodeLists;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of CompositionStep to insert code lists of a referenced questionnaire.
 */
@Slf4j
class InsertCodeLists implements CompositionStep {

    /** Host questionnaire. */
    private Questionnaire questionnaire;
    /** Host questionnaire code list ids. */
    private final Set<String> codeListIds = new HashSet<>();

    /**
     * Insert code lists of the referenced questionnaire in the referencing questionnaire.
     * If a code list of the referenced questionnaire has the same id as a list in the referencing questionnaire,
     * the code list is not added.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
        //
        this.questionnaire = questionnaire;
        //
        CodeLists refCodeLists = referencedQuestionnaire.getCodeLists();
        if (refCodeLists == null) {
            log.debug("No code lists in referenced questionnaire '{}'", referencedQuestionnaire.getId());
            return;
        }
        //
        hostCodeLists();
        //
        if (questionnaire.getCodeLists() == null)
            questionnaire.setCodeLists(new CodeLists());
        //
        refCodeLists.getCodeList().forEach(codeList -> {
            if (codeListIds.contains(codeList.getId())) {
                log.debug("Code list with id '{}' is already in host questionnaire '{}', " +
                                "so it has not been inserted from reference '{}'",
                        codeList.getId(), questionnaire.getId(), referencedQuestionnaire.getId());
                return;
            }
            questionnaire.getCodeLists().getCodeList().add(codeList);

        });
        log.debug("Code lists from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

    private void hostCodeLists() {
        if (questionnaire.getCodeLists() != null)
            questionnaire.getCodeLists().getCodeList().forEach(codeList -> codeListIds.add(codeList.getId()));
    }

}
