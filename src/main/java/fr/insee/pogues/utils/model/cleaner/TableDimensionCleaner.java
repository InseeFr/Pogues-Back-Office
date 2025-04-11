package fr.insee.pogues.utils.model.cleaner;

import fr.insee.pogues.model.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fr.insee.pogues.utils.model.question.Table.*;


/**
 * TableDimensionCleaner is utils class
 * This cleaner convert old dynamic dimension modeling to new dynamic modeling.
 * Actually, both are based on String object (ugly)
 */
public class TableDimensionCleaner implements ModelCleaner {

    @Override
    public void apply(Questionnaire questionnaire) {
        convertDynamicTableDimension(questionnaire);
    }

    private void convertDynamicTableDimension(ComponentType poguesComponent){
        if(poguesComponent instanceof SequenceType sequence){
            sequence.getChild().forEach(this::convertDynamicTableDimension);
        }
        if(poguesComponent instanceof QuestionType question && QuestionTypeEnum.TABLE.equals(question.getQuestionType())){
            convertDynamicTableDimension(question);
        }
    }

    /**
     * In old model, there is 3 cases:
     * <ul>
     *     <li>'0' -> NON_DYNAMIC</li>
     *     <li>'m-' : min=m & no max -> DYNAMIC_LENGTH</li>
     *     <li>'-n' : no min & max=n -> DYNAMIC_LENGTH</li>
     *     <li>'m-n' : min=m & max=n -> DYNAMIC_LENGTH</li>
     * </ul>
     * This function translate old modelisation to new modelisation.
     * We assume that min or max is not defined, we set dimension to NON_DYNAMIC
     * @param tableQuestion
     */
    private void convertDynamicTableDimension(QuestionType tableQuestion){
        Optional<DimensionType> primaryDimension = tableQuestion.getResponseStructure()
                .getDimension().stream()
                .filter(d -> DimensionTypeEnum.PRIMARY.equals(d.getDimensionType()))
                .findFirst();
        if(primaryDimension.isPresent()){
            DimensionType foundDimension = primaryDimension.get();
            String dynamic = foundDimension.getDynamic();
            if(!List.of(NON_DYNAMIC_DIMENSION, DYNAMIC_LENGTH_DIMENSION, FIXED_LENGTH_DIMENSION).contains(dynamic)){
                List<Integer> minMax = Arrays.stream(dynamic.split("-"))
                        .filter(value -> !value.isEmpty())
                        .map(Integer::parseInt).toList();
                if(minMax.size() == 2) {
                    foundDimension.setMinLines(BigInteger.valueOf(minMax.get(0)));
                    foundDimension.setMaxLines(BigInteger.valueOf(minMax.get(1)));
                    foundDimension.setDynamic(DYNAMIC_LENGTH_DIMENSION);
                } else foundDimension.setDynamic(NON_DYNAMIC_DIMENSION);
            }
        }

        // remove existing dynamic attribute for non-primary dimension
        tableQuestion.getResponseStructure()
                .getDimension().stream()
                .filter(d -> !DimensionTypeEnum.PRIMARY.equals(d.getDimensionType()))
                .forEach(dimension -> dimension.setDynamic(null));
    }
}
