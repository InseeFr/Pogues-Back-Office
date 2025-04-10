package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ModelCleaningServiceTest {

    private ModelCleaningService modelCleaningService;

    private ControlType createFakeControle(ControlCriticityEnum criticity){
        ControlType control = new ControlType();
        control.setCriticity(criticity);
        return control;
    }

    @BeforeEach
    void initService(){
        modelCleaningService = new ModelCleaningService();
    }

    @Test
    @DisplayName("Should change INFO criticty to WARN")
    void should_changeCriticityInfoWarn(){
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();
        sequence.getControl().add(createFakeControle(ControlCriticityEnum.INFO));

        QuestionType inputNumber = new QuestionType();
        inputNumber.getControl().addAll(
                List.of(createFakeControle(ControlCriticityEnum.INFO),
                        createFakeControle(ControlCriticityEnum.INFO)));
        sequence.getChild().add(inputNumber);
        questionnaire.getChild().add(sequence);

        modelCleaningService.changeControlCriticityInfoToWarn(questionnaire);
        SequenceType sequenceChanged = (SequenceType) questionnaire.getChild().getFirst();
        QuestionType inputNumberChanged = (QuestionType) sequenceChanged.getChild().getFirst();

        assertEquals(ControlCriticityEnum.WARN, sequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(0).getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(1).getCriticity());
    }

    @Test
    @DisplayName("Should keep other value of criticty (WARN and ERROR)")
    void should_keepOtherCriticity(){
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();
        sequence.getControl().add(createFakeControle(ControlCriticityEnum.ERROR));

        QuestionType inputNumber = new QuestionType();
        inputNumber.getControl().addAll(
                List.of(createFakeControle(ControlCriticityEnum.WARN),
                        createFakeControle(ControlCriticityEnum.ERROR)));
        sequence.getChild().add(inputNumber);
        questionnaire.getChild().add(sequence);

        modelCleaningService.changeControlCriticityInfoToWarn(questionnaire);
        SequenceType sequenceChanged = (SequenceType) questionnaire.getChild().getFirst();
        QuestionType inputNumberChanged = (QuestionType) sequenceChanged.getChild().getFirst();

        assertEquals(ControlCriticityEnum.ERROR, sequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(0).getCriticity());
        assertEquals(ControlCriticityEnum.ERROR, inputNumberChanged.getControl().get(1).getCriticity());
    }

}
