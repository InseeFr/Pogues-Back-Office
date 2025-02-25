package fr.insee.pogues.utils.model;

import fr.insee.pogues.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.ModelCreatorUtils.*;
import static fr.insee.pogues.utils.model.Variables.buildVariablesBasedOnTwoDimensions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariablesTest {

    private List<CodeType> primaryCodes;
    private List<CodeType> secondaryCodes;

    @BeforeEach
    void initCodesList(){
        this.primaryCodes = List.of(
                initFakeCodeType("H","Homme",""),
                initFakeCodeType("F","Femme",""),
                initFakeCodeType("NB","Non-binaire","")
        );
        this.secondaryCodes = List.of(
                initFakeCodeType("1","Oui",""),
                initFakeCodeType("1","Non","")
        );
    }

    @Test
    void testBuildVariablesWithSecondaryAxis(){
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );
        List<VariableType> variables = buildVariablesBasedOnTwoDimensions(
                primaryCodes, secondaryCodes, responses, "SUPER_QUESTION", code -> code.getLabel()
        );
        assertEquals(primaryCodes.size()*secondaryCodes.size(),
                variables.size());
        // check mapping between ids of variables and responses
        responses.forEach(response -> {
            assertTrue(variables.stream().anyMatch(v->response.getCollectedVariableReference().equals(v.getId())));
        });
    }

    @Test
    void testBuildSimpleVariablesForMultipleChoiceQuestion(){
        // TODO
    }

    @Test
    void testBuildVariablesAccordingToOneAndTwoMeasures(){
        List<DimensionType> measures = List.of(
                createSimpleMeasureDimension("Name ?"),
                createSimpleMeasureDimension("Last Name ?"));
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );

        List<VariableType> variables = buildVariablesBasedOnTwoDimensions(
                primaryCodes, measures, responses, "SUPER_QUESTION", measure -> measure.getLabel());
        assertEquals(6, variables.size());
        // Test if each responses id are mapped with variable
        responses.forEach(response -> {
            assertTrue(variables.stream().anyMatch(v->response.getCollectedVariableReference().equals(v.getId())));
        });
    }
}
