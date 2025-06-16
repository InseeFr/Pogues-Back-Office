package fr.insee.pogues.service;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static fr.insee.pogues.utils.model.question.Table.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class ModelCleaningServiceTest {

    private ModelCleaningService modelCleaningService;

    private ControlType createFakeControle(ControlCriticityEnum criticity){
        ControlType control = new ControlType();
        control.setCriticity(criticity);
        return control;
    }


    private DimensionType createFakeDimension(String dynamic, DimensionTypeEnum dimensionType){
        DimensionType dimension = new DimensionType();
        dimension.setDynamic(dynamic);
        dimension.setDimensionType(dimensionType);
        return dimension;
    }

    private DimensionType createFakeDimension(String dynamic, DimensionTypeEnum dimensionType, BigInteger min, BigInteger max){
        DimensionType dimension = new DimensionType();
        dimension.setDynamic(dynamic);
        dimension.setDimensionType(dimensionType);
        dimension.setMinLines(min);
        dimension.setMaxLines(max);
        return dimension;
    }

    private DimensionType createFakeDimension(String dynamic, DimensionTypeEnum dimensionType, String fixedLength){
        DimensionType dimension = new DimensionType();
        dimension.setDynamic(dynamic);
        dimension.setDimensionType(dimensionType);
        ExpressionType size = new ExpressionType();
        size.setValue(fixedLength);
        dimension.setFixedLength(size);
        return dimension;
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

        modelCleaningService.cleanModel(questionnaire);
        SequenceType sequenceChanged = (SequenceType) questionnaire.getChild().getFirst();
        QuestionType inputNumberChanged = (QuestionType) sequenceChanged.getChild().getFirst();

        assertEquals(ControlCriticityEnum.WARN, sequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(0).getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(1).getCriticity());
    }

    @Test
    @DisplayName("Should change INFO criticty to WARN deep inside questionnaire")
    void should_changeCriticityInfoWarnDeep(){
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();
        sequence.getControl().add(createFakeControle(ControlCriticityEnum.INFO));

        QuestionType questionSeqLevel = new QuestionType();
        questionSeqLevel.getControl().add(createFakeControle(ControlCriticityEnum.INFO));
        sequence.getChild().add(questionSeqLevel);

        SequenceType subSequence = new SequenceType();
        subSequence.getControl().add(createFakeControle(ControlCriticityEnum.INFO));

        QuestionType questionSubSeqLevel = new QuestionType();
        questionSubSeqLevel.getControl().add(createFakeControle(ControlCriticityEnum.INFO));

        subSequence.getChild().add(questionSubSeqLevel);
        sequence.getChild().add(subSequence);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);
        SequenceType sequenceChanged = (SequenceType) questionnaire.getChild().getFirst();
        QuestionType questionSeqLevelChanged = (QuestionType) sequenceChanged.getChild().getFirst();
        SequenceType subSequenceChanged = (SequenceType) sequenceChanged.getChild().stream().filter(componentType -> componentType instanceof SequenceType).findFirst().get();
        QuestionType questionSubSeqLevelChanged = (QuestionType) subSequenceChanged.getChild().getFirst();

        assertEquals(ControlCriticityEnum.WARN, sequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, questionSeqLevelChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, subSequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, questionSubSeqLevelChanged.getControl().getFirst().getCriticity());
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

        modelCleaningService.cleanModel(questionnaire);
        SequenceType sequenceChanged = (SequenceType) questionnaire.getChild().getFirst();
        QuestionType inputNumberChanged = (QuestionType) sequenceChanged.getChild().getFirst();

        assertEquals(ControlCriticityEnum.ERROR, sequenceChanged.getControl().getFirst().getCriticity());
        assertEquals(ControlCriticityEnum.WARN, inputNumberChanged.getControl().get(0).getCriticity());
        assertEquals(ControlCriticityEnum.ERROR, inputNumberChanged.getControl().get(1).getCriticity());
    }

    @Test
    @DisplayName("Should convert old modelisation dynamic dimension on Table where min or max or both are not defined")
    void should_convertDimension() {
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();

        QuestionType tableQuestion0 = new QuestionType();
        tableQuestion0.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure0 = new ResponseStructureType();
        responseStructure0.getDimension().add(createFakeDimension("-", DimensionTypeEnum.PRIMARY));
        tableQuestion0.setResponseStructure(responseStructure0);

        QuestionType tableQuestion1 = new QuestionType();
        tableQuestion1.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure1 = new ResponseStructureType();
        responseStructure1.getDimension().add(createFakeDimension("3-", DimensionTypeEnum.PRIMARY));
        tableQuestion1.setResponseStructure(responseStructure1);


        QuestionType tableQuestion2 = new QuestionType();
        tableQuestion2.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure2 = new ResponseStructureType();
        responseStructure2.getDimension().add(createFakeDimension("-4", DimensionTypeEnum.PRIMARY));
        tableQuestion2.setResponseStructure(responseStructure2);

        sequence.getChild().add(tableQuestion0);
        sequence.getChild().add(tableQuestion1);
        sequence.getChild().add(tableQuestion2);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);

        QuestionType tableQuestionChanged0 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(0);
        QuestionType tableQuestionChanged1 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(1);
        QuestionType tableQuestionChanged2 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(2);
        assertEquals("NON_DYNAMIC", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getDynamic());
        assertEquals("NON_DYNAMIC", tableQuestionChanged1.getResponseStructure().getDimension().getFirst().getDynamic());
        assertEquals("NON_DYNAMIC", tableQuestionChanged2.getResponseStructure().getDimension().getFirst().getDynamic());
    }

    @Test
    @DisplayName("Should convert old modelisation dynamic dimension as 0 to NON_DYNAMIC on Table")
    void should_convertDimensionWithAsDimension(){
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();

        QuestionType tableQuestion0 = new QuestionType();
        tableQuestion0.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure0 = new ResponseStructureType();
        responseStructure0.getDimension().add(createFakeDimension("0", DimensionTypeEnum.PRIMARY));
        responseStructure0.getDimension().add(createFakeDimension("0", DimensionTypeEnum.MEASURE));
        tableQuestion0.setResponseStructure(responseStructure0);

        sequence.getChild().add(tableQuestion0);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);

        QuestionType tableQuestionChanged0 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().getFirst();
        assertEquals("NON_DYNAMIC", tableQuestionChanged0.getResponseStructure().getDimension().get(0).getDynamic());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().get(1).getDynamic());
    }

    @Test
    @DisplayName("Should convert old modelisation dynamic dimension on Table where min & max are defined")
    void should_convertDimensionDynamic() {
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();

        QuestionType tableQuestion0 = new QuestionType();
        tableQuestion0.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure0 = new ResponseStructureType();
        responseStructure0.getDimension().add(createFakeDimension("1-5", DimensionTypeEnum.PRIMARY));
        tableQuestion0.setResponseStructure(responseStructure0);
        sequence.getChild().add(tableQuestion0);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);

        QuestionType tableQuestionChanged0 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().getFirst();
        assertEquals("DYNAMIC_LENGTH", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getDynamic());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMinLines());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMaxLines());
        assertEquals("1", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMinimum().getValue());
        assertEquals("5", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMaximum().getValue());
    }

    @Test
    @DisplayName("Should not convert modelisation when dynamic is always in new modelisation")
    void should_notConvertDimension() {
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();

        QuestionType tableQuestion0 = new QuestionType();
        tableQuestion0.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure0 = new ResponseStructureType();
        responseStructure0.getDimension().add(createFakeDimension(NON_DYNAMIC_DIMENSION, DimensionTypeEnum.PRIMARY));
        tableQuestion0.setResponseStructure(responseStructure0);

        QuestionType tableQuestion1 = new QuestionType();
        tableQuestion1.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure1 = new ResponseStructureType();
        responseStructure1.getDimension().add(createFakeDimension(DYNAMIC_LENGTH_DIMENSION, DimensionTypeEnum.PRIMARY));
        tableQuestion1.setResponseStructure(responseStructure1);


        QuestionType tableQuestion2 = new QuestionType();
        tableQuestion2.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure2 = new ResponseStructureType();
        responseStructure2.getDimension().add(createFakeDimension(FIXED_LENGTH_DIMENSION, DimensionTypeEnum.PRIMARY));
        tableQuestion2.setResponseStructure(responseStructure2);

        sequence.getChild().add(tableQuestion0);
        sequence.getChild().add(tableQuestion1);
        sequence.getChild().add(tableQuestion2);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);

        QuestionType tableQuestionChanged0 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(0);
        QuestionType tableQuestionChanged1 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(1);
        QuestionType tableQuestionChanged2 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(2);
        assertEquals("NON_DYNAMIC", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getDynamic());
        assertEquals("DYNAMIC_LENGTH", tableQuestionChanged1.getResponseStructure().getDimension().getFirst().getDynamic());
        assertEquals("DYNAMIC_FIXED", tableQuestionChanged2.getResponseStructure().getDimension().getFirst().getDynamic());
    }

    @Test
    @DisplayName("Should convert min max integer to number object")
    void should_updateMinMaxToTypedValue(){
        Questionnaire questionnaire = new Questionnaire();
        SequenceType sequence = new SequenceType();

        QuestionType tableQuestion0 = new QuestionType();
        tableQuestion0.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure0 = new ResponseStructureType();
        responseStructure0.getDimension().add(createFakeDimension(DYNAMIC_LENGTH_DIMENSION, DimensionTypeEnum.PRIMARY,
                BigInteger.valueOf(1), BigInteger.valueOf(5)));
        tableQuestion0.setResponseStructure(responseStructure0);

        QuestionType tableQuestion1 = new QuestionType();
        tableQuestion1.setQuestionType(QuestionTypeEnum.TABLE);
        ResponseStructureType responseStructure1 = new ResponseStructureType();
        responseStructure1.getDimension().add(createFakeDimension(FIXED_LENGTH_DIMENSION, DimensionTypeEnum.PRIMARY, "4+2"));
        tableQuestion1.setResponseStructure(responseStructure1);

        sequence.getChild().add(tableQuestion0);
        sequence.getChild().add(tableQuestion1);
        questionnaire.getChild().add(sequence);

        modelCleaningService.cleanModel(questionnaire);

        QuestionType tableQuestionChanged0 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(0);
        QuestionType tableQuestionChanged1 = (QuestionType) ((SequenceType) questionnaire.getChild().getFirst()).getChild().get(1);

        assertEquals("DYNAMIC_LENGTH", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getDynamic());
        assertEquals("DYNAMIC_FIXED", tableQuestionChanged1.getResponseStructure().getDimension().getFirst().getDynamic());

        assertEquals("1", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMinimum().getValue());
        assertEquals(ValueTypeEnum.NUMBER, tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMinimum().getType());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMinLines());
        assertEquals("5", tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMaximum().getValue());
        assertEquals(ValueTypeEnum.NUMBER, tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMaximum().getType());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getMaxLines());


        assertEquals("4+2", tableQuestionChanged1.getResponseStructure().getDimension().getFirst().getSize().getValue());
        assertEquals(ValueTypeEnum.VTL, tableQuestionChanged1.getResponseStructure().getDimension().getFirst().getSize().getType());
        assertNull(tableQuestionChanged0.getResponseStructure().getDimension().getFirst().getFixedLength());
    }


}
