package fr.insee.pogues.utils.model.question;

import fr.insee.pogues.model.CodeType;
import fr.insee.pogues.model.DatatypeTypeEnum;
import fr.insee.pogues.model.MappingType;
import fr.insee.pogues.model.ResponseType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static fr.insee.pogues.utils.ModelCreatorUtils.createResponse;
import static fr.insee.pogues.utils.ModelCreatorUtils.initFakeCodeType;
import static fr.insee.pogues.utils.model.question.Common.buildMappingsBasedOnTwoDimensions;
import static fr.insee.pogues.utils.model.question.Common.buildSimpleMappingForMultipleChoiceQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonTest {

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
    void testBuildMappingsWithSecondaryAxis(){
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );
        List<MappingType> mappings = buildMappingsBasedOnTwoDimensions(
                primaryCodes, secondaryCodes, responses
        );
        assertEquals(primaryCodes.size()*secondaryCodes.size(),
                mappings.size());
        assertTrue(mappings.stream()
                        .anyMatch(m-> responses.getFirst().getId()
                                .equals(m.getMappingSource()))
        );
        assertEquals("1 1",mappings.getFirst().getMappingTarget());
        assertEquals(
                String.format("%d %d", secondaryCodes.size(), primaryCodes.size()),
                mappings.getLast().getMappingTarget());
    }

    @Test
    void testBuildSimpleMappingForMultipleChoiceQuestion(){
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.BOOLEAN),
                createResponse(DatatypeTypeEnum.BOOLEAN),
                createResponse(DatatypeTypeEnum.BOOLEAN)
        );
        List<MappingType> mappings = buildSimpleMappingForMultipleChoiceQuestion(responses);
        assertEquals(responses.size(),mappings.size());
        assertTrue(mappings.stream()
                .anyMatch(m-> responses.getFirst().getId()
                        .equals(m.getMappingSource()))
        );
        assertEquals("1",mappings.getFirst().getMappingTarget());
        assertEquals("3", mappings.getLast().getMappingTarget());
    }

    @Test
    void testBuildMappingsAccordingToOneAndTwoMeasures(){
        List<ResponseType> responsesPattern = List.of(
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.TEXT));
        List<ResponseType> responses = List.of(
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.NUMERIC),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT),
                createResponse(DatatypeTypeEnum.TEXT)
        );
        List<MappingType> mappings = buildMappingsBasedOnTwoDimensions(primaryCodes, responsesPattern, responses);
        assertEquals(6, mappings.size());
        assertEquals("1 1", mappings.getFirst().getMappingTarget());
        assertEquals("2 3", mappings.getLast().getMappingTarget());
        // Test if each responses id are mapped
        responses.forEach(response -> {
            assertTrue(mappings.stream().anyMatch(m->response.getId().equals(m.getMappingSource())));
        });
    }
}
