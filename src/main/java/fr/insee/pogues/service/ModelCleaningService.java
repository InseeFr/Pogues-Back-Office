package fr.insee.pogues.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.model.*;
import fr.insee.pogues.utils.PoguesDeserializer;
import fr.insee.pogues.utils.PoguesSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;
import static fr.insee.pogues.utils.model.question.Table.*;

@Service
@Slf4j
public class ModelCleaningService {

    public JsonNode cleanModel(JsonNode jsonNodeQuestionnaire) throws JAXBException, UnsupportedEncodingException, JsonProcessingException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonNodeQuestionnaire);
        changeControlCriticityInfoToWarn(questionnaire);
        convertDynamicTableDimension(questionnaire);
        return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire));
    }



    public void convertDynamicTableDimension(ComponentType poguesComponent){
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
            if(!List.of(NON_DYNAMIC_DIMENSION, DYNAMIC_LENGTH_DIMENSION, FIXED_LENGTH_DIMENSION).contains(dynamic) && dynamic.contains("-")){
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
