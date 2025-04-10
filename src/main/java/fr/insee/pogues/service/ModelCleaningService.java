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

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

@Service
@Slf4j
public class ModelCleaningService {

    public ModelCleaningService() {}

    public JsonNode cleanModel(JsonNode jsonNodeQuestionnaire) throws JAXBException, UnsupportedEncodingException, JsonProcessingException {
        Questionnaire questionnaire = PoguesDeserializer.questionnaireToJavaObject(jsonNodeQuestionnaire);
        changeControlCriticityInfoToWarn(questionnaire);
        return jsonStringtoJsonNode(PoguesSerializer.questionnaireJavaToString(questionnaire));
    }


    public void changeControlCriticityInfoToWarn(ComponentType poguesComponent){
        // Questionnaire is an extension of sequenceType
        if(poguesComponent instanceof SequenceType sequence){
            sequence.getChild().forEach(this::changeControlCriticityInfoToWarn);
        }
        poguesComponent.getControl().forEach(this::changeControlCriticityInfoToWarn);
    }


    private void changeControlCriticityInfoToWarn(ControlType controlType){
        if(ControlCriticityEnum.INFO.equals(controlType.getCriticity())) controlType.setCriticity(ControlCriticityEnum.WARN);
    }
}
