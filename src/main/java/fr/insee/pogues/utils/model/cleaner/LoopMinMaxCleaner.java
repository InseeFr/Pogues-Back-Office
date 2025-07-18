package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.Questionnaire;

/**
 * Model cleaning step for a modeling change on loop min/max properties.
 * Old properties in loops (DynamicIterationType): "Minimum", "Maximum".
 * New ones: "minimum", "maximum", "size".
 */
public class LoopMinMaxCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        if (questionnaire.getIterations() == null)
            return;
        questionnaire.getIterations().getIteration().stream()
                // the min/max props are on the DynamicIterationType subclass
                .filter(DynamicIterationType.class::isInstance).map(DynamicIterationType.class::cast)
                .forEach(loop -> {
                    // For now, we need to have both new and old modeling to be backward compatible towards Eno Xml
                    oldToNew(loop);
                    newToOld(loop);
                    fixedLengthConsistency(loop);
                });
    }

    /** If old Minimum/Maximum properties are present,
     * they are kept, and the now props are valorized from the old ones. */
    private void oldToNew(DynamicIterationType loop) {
        if (loop.getDeprecatedMinimum() != null)
            loop.setMinimum(loop.getDeprecatedMinimum());
        if (loop.getDeprecatedMaximum() != null)
            loop.setMaximum(loop.getDeprecatedMaximum());
    }

    /** If the loop has the new props, its values are copied onto the old ones. */
    private void newToOld(DynamicIterationType loop) {
        if (loop.getSize() != null) {
            loop.setDeprecatedMinimum(loop.getSize());
            loop.setDeprecatedMaximum(loop.getSize());
        }
        if (loop.getMinimum() != null)
            loop.setDeprecatedMinimum(loop.getMinimum());
        if (loop.getMaximum() != null)
            loop.setDeprecatedMaximum(loop.getMaximum());
    }

    private void fixedLengthConsistency(DynamicIterationType loop) {
        if (loop.isIsFixedLength() != null && loop.isIsFixedLength()) {
            loop.setMinimum(null);
            loop.setMaximum(null);
        } else {
            loop.setSize(null);
        }
    }

}
