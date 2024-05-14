package fr.insee.pogues.transforms.visualize.composition;

import fr.insee.pogues.model.ComponentType;
import fr.insee.pogues.model.Questionnaire;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.insee.pogues.utils.PoguesModelUtils.getSequences;

/**
 * Implementation of CompositionStep to replace questionnaire reference by its sequences.
 */
@Slf4j
class InsertSequences implements CompositionStep {

    /**
     * Replace questionnaire reference by its sequences.
     * @param questionnaire Referencing questionnaire.
     * @param referencedQuestionnaire Referenced questionnaire.
     */
    @Override
    public void apply(Questionnaire questionnaire, Questionnaire referencedQuestionnaire) {
        //
        List<ComponentType> refSequences = getSequences(referencedQuestionnaire);
        int indexOfModification = 0;
        for (ComponentType seq : questionnaire.getChild()) {
            if (seq.getId().equals(referencedQuestionnaire.getId())) {
                break;
            }
            indexOfModification++;
        }
        log.debug("Index to modify {}", indexOfModification);
        // Suppression of the questionnaire reference
        questionnaire.getChild().remove(indexOfModification);
        // Insertion of the sequences
        for (int i=0; i<refSequences.size();i++) {
            questionnaire.getChild().add(indexOfModification, referencedQuestionnaire.getChild().get(refSequences.size()-1-i));
        }
        //
        log.info("Sequences from '{}' inserted in '{}'", referencedQuestionnaire.getId(), questionnaire.getId());
    }

}
