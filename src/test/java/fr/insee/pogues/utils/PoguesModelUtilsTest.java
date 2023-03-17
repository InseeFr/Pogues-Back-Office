package fr.insee.pogues.utils;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.IllegalIterationException;
import fr.insee.pogues.model.DynamicIterationType;
import fr.insee.pogues.model.FlowControlType;
import fr.insee.pogues.model.IterationType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PoguesModelUtilsTest {

    @Test
    void getFlowControlBounds_nullIfTrue() {
        FlowControlType flowControlType = new FlowControlType();
        assertThrows(IllegalFlowControlException.class, () -> PoguesModelUtils.getFlowControlBounds(flowControlType));
    }

    @Test
    void getIterationBounds_invalidIfTrue() {
        FlowControlType flowControlType = new FlowControlType();
        flowControlType.setIfTrue("foo");
        assertThrows(IllegalFlowControlException.class, () -> PoguesModelUtils.getFlowControlBounds(flowControlType));
    }

    @Test
    void getIterationBounds_emptyMemberReference() {
        IterationType iterationType = new DynamicIterationType();
        assertThrows(IllegalIterationException.class, () -> PoguesModelUtils.getIterationBounds(iterationType));
    }

    @Test
    void getIterationBounds_invalidMemberReference() {
        IterationType iterationType = new DynamicIterationType();
        iterationType.getMemberReference().add("seq1");
        iterationType.getMemberReference().add("seq2");
        iterationType.getMemberReference().add("seq3");
        assertThrows(IllegalIterationException.class, () -> PoguesModelUtils.getIterationBounds(iterationType));
    }

}
