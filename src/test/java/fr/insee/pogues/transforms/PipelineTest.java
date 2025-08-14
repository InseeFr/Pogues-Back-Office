package fr.insee.pogues.transforms;

import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.service.ModelCleaningService;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesJSONDerefImpl;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static fr.insee.pogues.utils.IOStreamsUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelineTest {

    @Test
    void passesThroughTest() throws Exception {
        byte[] inputBytes = "".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(inputBytes);
        PipeLine pipeline = new PipeLine();
        ByteArrayOutputStream output = pipeline.from(input).transform();
        assertEquals(new String(inputBytes), new String(output.toByteArray()));
    }

    @Test
    void mapsTest() throws Exception {
        String input = "";
        PipeLine.Transform<InputStream, ByteArrayOutputStream> t0 = (InputStream i, Map<String, Object> params,String survey) -> {
            String resultAsString = inputStream2String(i) + "concat";
            return string2BOAS(resultAsString);
        };
        PipeLine pipeline = new PipeLine();
        ByteArrayOutputStream output = pipeline.from(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)))
                .map(t0, null,null).transform();
        assertEquals("concat", new String(output.toByteArray()));
    }

    @Test
    void throwsExceptionTest() {
        String input = "";
        PipeLine.Transform<InputStream, ByteArrayOutputStream> t0 = (InputStream i, Map<String, Object> params,String survey) -> {
            throw new PoguesException(500, "Expected error", "Mapping function exception correctly propagates through pipeline");
        };
        PipeLine pipeline = new PipeLine();
        assertThrows(RuntimeException.class, () -> pipeline
                .from(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)))
                .map(t0, null,null).transform());
    }

    @Test
    void pipelineWithCleaningAndDereferencing() throws Exception {
        // Given
        String json = """
                {
                  "id" : "lmyoceix",
                  "genericName" : "QUESTIONNAIRE",
                  "agency" : "fr.insee",
                  "final" : false,
                  "flowLogic" : "FILTER",
                  "formulasLanguage" : "VTL",
                  "lastUpdatedDate" : "Mon Sep 25 2023 11:01:43 GMT+0200 (heure d’été d’Europe centrale)",
                  "owner" : "ENO-INTEGRATION-TESTS",
                  "Name" : "ENOSIMPLE",
                  "Label" : [ "Eno - Simple questionnaire" ],
                  "Control" : [ ],
                  "FlowControl" : [ ],
                  "TargetMode" : [ "CAPI", "CATI", "PAPI", "CAWI" ],
                  "Child" : [ {
                    "id" : "lmynuv39",
                    "depth" : 1,
                    "genericName" : "MODULE",
                    "type" : "SequenceType",
                    "Name" : "S1",
                    "Label" : [ "\\"Unique sequence\\"" ],
                    "Declaration" : [ ],
                    "Control" : [ ],
                    "FlowControl" : [ ],
                    "TargetMode" : [ "CAPI", "CATI", "PAPI", "CAWI" ],
                    "Child" : [ {
                      "id" : "lmyo3e0y",
                      "questionType" : "SIMPLE",
                      "type" : "QuestionType",
                      "Name" : "Q1",
                      "Label" : [ "\\"Unique question\\"" ],
                      "Declaration" : [ ],
                      "Control" : [ ],
                      "FlowControl" : [ ],
                      "TargetMode" : [ "CAPI", "CATI", "PAPI", "CAWI" ],
                      "Response" : [ {
                        "id" : "lmynvhl8",
                        "mandatory" : false,
                        "Datatype" : {
                          "typeName" : "TEXT",
                          "type" : "TextDatatypeType",
                          "MaxLength" : 249
                        },
                        "CollectedVariableReference" : "lmyo22nw"
                      } ]
                    } ]
                  }, {
                    "id" : "idendquest",
                    "depth" : 1,
                    "genericName" : "MODULE",
                    "type" : "SequenceType",
                    "Name" : "QUESTIONNAIRE_END",
                    "Label" : [ "QUESTIONNAIRE_END" ],
                    "Declaration" : [ ],
                    "Control" : [ ],
                    "FlowControl" : [ ],
                    "TargetMode" : [ "CAPI", "CATI", "PAPI", "CAWI" ],
                    "Child" : [ ]
                  } ],
                  "DataCollection" : [ {
                    "id" : "TCM",
                    "uri" : "http://ddi:fr.insee:DataCollection.TCM"
                  } ],
                  "ComponentGroup" : [ {
                    "id" : "lmynyac5",
                    "Name" : "PAGE_1",
                    "Label" : [ "Components for page 1" ],
                    "MemberReference" : [ "idendquest", "lmynuv39", "lmyo3e0y" ]
                  } ],
                  "CodeLists" : {
                    "CodeList" : [ ]
                  },
                  "Variables" : {
                    "Variable" : [ {
                      "id" : "lmyo22nw",
                      "type" : "CollectedVariableType",
                      "Datatype" : {
                        "typeName" : "TEXT",
                        "type" : "TextDatatypeType",
                        "MaxLength" : 249
                      },
                      "Name" : "Q1",
                      "Label" : "Q1 label"
                    } ]
                  },
                  "childQuestionnaireRef" : [ ]
                }""";
        Map<String, Object> params = new HashMap<>();
        params.put("needDeref", false);
        var modelCleaningService = new ModelCleaningService();
        var jsonToJsonDeref = new PoguesJSONToPoguesJSONDerefImpl();

        // When
        ByteArrayOutputStream outputStream = new PipeLine().from(string2InputStream(json))
                .map(modelCleaningService::transform, null, null)
                .map(jsonToJsonDeref::transform, params, null)
                .transform();

        // Then
        JSONAssert.assertEquals(json, outputStream.toString(), JSONCompareMode.LENIENT);
    }
}
