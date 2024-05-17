package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesJSONDeref;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.DDIToLunaticJSON;
import fr.insee.pogues.transforms.visualize.eno.DDIToXForms;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriQueen;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriStromaeV2;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriStromaeV3;
import fr.insee.pogues.transforms.visualize.uri.XFormsToURIStromaeV1;
import fr.insee.pogues.utils.suggester.SuggesterVisuTreatment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static fr.insee.pogues.transforms.visualize.ModelMapper.output2Input;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2InputStream;

@RestController
@RequestMapping("/api/transform")
@Tag(name = "5. Visualization with URI")
@Slf4j
public class VisualizeWithURI {

    @Autowired
    PoguesJSONToPoguesXML jsonToXML;

    @Autowired
    PoguesXMLToDDI poguesXMLToDDI;

    @Autowired
    DDIToXForms ddiToXForm;

    @Autowired
    XFormsToURIStromaeV1 xformToUri;

    @Autowired
    DDIToLunaticJSON ddiToLunaticJSON;

    @Autowired
    LunaticJSONToUriQueen lunaticJSONToUriQueen;

    @Autowired
    LunaticJSONToUriStromaeV2 lunaticJSONToUriStromaeV2;

    @Autowired
    LunaticJSONToUriStromaeV3 lunaticJSONToUriStromaeV3;

    @Autowired
    PoguesJSONToPoguesJSONDeref jsonToJsonDeref;

    private static final String CONTENT_DISPOSITION = HttpHeaders.CONTENT_DISPOSITION;

    @PostMapping(path = "visualize/{dataCollection}/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI from JSON serialized Pogues entity", description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON representation of the Pogues Model")
    public ResponseEntity<String> visualizeFromBody(@RequestBody String request,
                                                 @PathVariable(value = "dataCollection") String dataCollection,
                                                 @PathVariable(value = "questionnaire") String questionnaire,
                                                 @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("dataCollection", dataCollection.toLowerCase());
        params.put("questionnaire", questionnaire.toLowerCase());
        params.put("needDeref", ref);
        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaire.toLowerCase())
                        .map(jsonToXML::transform, params, questionnaire.toLowerCase())
                        .map(poguesXMLToDDI::transform, params, questionnaire.toLowerCase())
                        .map(ddiToXForm::transform, params, questionnaire.toLowerCase())
                        .transform();

                uri = xformToUri.transform(output2Input(outputStream), params, questionnaire.toLowerCase());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "visualize-queen-telephone/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI CATI Queen from JSON serialized Pogues entity", description = "Get visualization URI CATI Queen from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeCatiQueenFromBody(@RequestBody String request,
                                                             @PathVariable(value = "questionnaire") String questionnaireName,
                                                             @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CATI");
        params.put("needDeref", ref);
        params.put("nomenclatureIds", SuggesterVisuTreatment.getNomenclatureIdsFromQuestionnaire(request));
        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                        .map(jsonToXML::transform, params, questionnaireName.toLowerCase())
                        .map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
                        .map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                        .transform();
                uri = lunaticJSONToUriQueen.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
            } catch (Exception e) {
                log.error(e.getCause().getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "visualize-queen/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI CAPI Queen from JSON serialized Pogues entity", description = "Get visualization URI CAPI Queen from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeQueenFromBody(@RequestBody String request,
                                                      @PathVariable(value = "questionnaire") String questionnaireName,
                                                      @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CAPI");
        params.put("needDeref", ref);
        params.put("nomenclatureIds", SuggesterVisuTreatment.getNomenclatureIdsFromQuestionnaire(request));
        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                        .map(jsonToXML::transform, params, questionnaireName.toLowerCase())
                        .map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
                        .map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                        .transform();
                uri = lunaticJSONToUriQueen.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
            } catch (Exception e) {
                log.error(e.getCause().getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "visualize-stromae-v2/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI Stromae V2 from JSON serialized Pogues entity", description = "Get visualization URI Stromae V2 from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeStromaeV2FromBody(@RequestBody String request,
                                                             @PathVariable(value = "questionnaire") String questionnaireName,
                                                             @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("questionnaire", questionnaireName.toLowerCase());
        params.put("needDeref", ref);
        params.put("mode", "CAWI");
        params.put("nomenclatureIds", SuggesterVisuTreatment.getNomenclatureIdsFromQuestionnaire(request));

        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                        .map(jsonToXML::transform, params, questionnaireName.toLowerCase())
                        .map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
                        .map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                        .transform();

                uri = lunaticJSONToUriStromaeV2.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
            } catch (Exception e) {
                log.error(e.getCause().getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "visualize-stromae-v3/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI Stromae V3 from JSON serialized Pogues entity", description = "Get visualization URI Stromae V3 from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeStromaeV3FromBody(@RequestBody String request,
                                                          @PathVariable(value = "questionnaire") String questionnaireName,
                                                          @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("questionnaire", questionnaireName.toLowerCase());
        params.put("needDeref", ref);
        params.put("mode", "CAWI");
        params.put("nomenclatureIds", SuggesterVisuTreatment.getNomenclatureIdsFromQuestionnaire(request));
        params.put("dsfr", true);
        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                        .map(jsonToXML::transform, params, questionnaireName.toLowerCase())
                        .map(poguesXMLToDDI::transform, params, questionnaireName.toLowerCase())
                        .map(ddiToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                        .transform();
                uri = lunaticJSONToUriStromaeV3.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
            } catch (Exception e) {
                log.error(e.getCause().getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "visualize-from-ddi/{dataCollection}/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI from DDI questionnaire", description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
    public ResponseEntity<String> visualizeFromDDIBody(@RequestBody String request,
                                                    @PathVariable(value = "dataCollection") String dataCollection,
                                                    @PathVariable(value = "questionnaire") String questionnaire) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("dataCollection", dataCollection.toLowerCase());
        params.put("questionnaire", questionnaire.toLowerCase());
        try {
            URI uri;
            try {
                ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                        .map(ddiToXForm::transform, params, questionnaire.toLowerCase())
                        .transform();
                uri = xformToUri.transform(output2Input(outputStream), params, questionnaire.toLowerCase());
            } catch (Exception e) {
                log.error(e.getCause().getMessage());
                throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping(path = "xform2uri/{dataCollection}/{questionnaire}", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get Pogues visualization URI From Pogues XForm document", description = "Returns the vizualisation URI of a form that was generated from XForm description found in body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Error") })
    public String xForm2URI(@RequestBody String questXforms,
                         @PathVariable(value = "dataCollection") String dataCollection,
                         @PathVariable(value = "questionnaire") String questionnaire) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("dataCollection", dataCollection.toLowerCase());
            params.put("questionnaire", questionnaire.toLowerCase());
            return xformToUri.transform(string2InputStream(questXforms), params, questionnaire).toString();
        } catch (Exception e) {
            log.error(e.getCause().getMessage());
            throw new PoguesException(500, e.getCause().getClass().getSimpleName(), e.getCause().getMessage());
        }
    }
}
