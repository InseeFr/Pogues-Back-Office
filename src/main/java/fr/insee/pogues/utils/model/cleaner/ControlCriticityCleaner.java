package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.*;

/**
 * ControlCriticityCleaner is utils class
 * This cleaner replace INFO criticy in ControlType to WARN criticty (based on enum)
 */
public class ControlCriticityCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        changeControlCriticityInfoToWarn(questionnaire);
    }

    public void changeControlCriticityInfoToWarn(ComponentType poguesComponent){
        // Questionnaire is an extension of sequenceType, subSequence is not different from Sequence in model
        if(poguesComponent instanceof SequenceType sequence){
            sequence.getChild().forEach(this::changeControlCriticityInfoToWarn);
        }
        poguesComponent.getControl().forEach(this::changeControlCriticityInfoToWarn);
    }


    private void changeControlCriticityInfoToWarn(ControlType controlType){
        if(ControlCriticityEnum.INFO.equals(controlType.getCriticity())) controlType.setCriticity(ControlCriticityEnum.WARN);
    }
}
