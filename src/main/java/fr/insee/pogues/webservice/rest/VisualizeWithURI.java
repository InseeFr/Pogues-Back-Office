package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.transforms.PipeLine;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesJSONDeref;
import fr.insee.pogues.transforms.visualize.PoguesJSONToPoguesXML;
import fr.insee.pogues.transforms.visualize.eno.DDIToXForms;
import fr.insee.pogues.transforms.visualize.eno.PoguesJSONToLunaticJSON;
import fr.insee.pogues.transforms.visualize.eno.PoguesXMLToDDI;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriQueen;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriStromaeV2;
import fr.insee.pogues.transforms.visualize.uri.LunaticJSONToUriStromaeV3;
import fr.insee.pogues.transforms.visualize.uri.XFormsToURIStromaeV1;
import fr.insee.pogues.utils.suggester.SuggesterVisuService;
import fr.insee.pogues.webservice.model.EnoContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import static fr.insee.pogues.utils.IOStreamsUtils.output2Input;
import static fr.insee.pogues.utils.IOStreamsUtils.string2InputStream;

@RestController
@RequestMapping("/api/transform")
@Tag(name = "6. Visualization with URI")
@AllArgsConstructor
@Slf4j
public class VisualizeWithURI {

    PoguesJSONToPoguesXML jsonToXML;
    PoguesXMLToDDI poguesXMLToDDI;
    DDIToXForms ddiToXForm;
    XFormsToURIStromaeV1 xformToUri;
    PoguesJSONToLunaticJSON poguesJSONToLunaticJSON;
    LunaticJSONToUriQueen lunaticJSONToUriQueen;
    LunaticJSONToUriStromaeV2 lunaticJSONToUriStromaeV2;
    LunaticJSONToUriStromaeV3 lunaticJSONToUriStromaeV3;
    PoguesJSONToPoguesJSONDeref jsonToJsonDeref;
    SuggesterVisuService suggesterVisuService;

    @PostMapping(path = "visualize/{dataCollection}/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI from JSON serialized Pogues entity", description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "JSON representation of the Pogues Model")
    public ResponseEntity<String> visualizeFromBody(
            @RequestBody String request,
            @PathVariable(value = "dataCollection") String dataCollection,
            @PathVariable(value = "questionnaire") String questionnaire,
            @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("dataCollection", dataCollection.toLowerCase());
        params.put("questionnaire", questionnaire.toLowerCase());
        params.put("needDeref", ref);
        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                .map(jsonToJsonDeref::transform, params, questionnaire.toLowerCase())
                .map(jsonToXML::transform, params, questionnaire.toLowerCase())
                .map(poguesXMLToDDI::transform, params, questionnaire.toLowerCase())
                .map(ddiToXForm::transform, params, questionnaire.toLowerCase())
                .transform();

        uri = xformToUri.transform(output2Input(outputStream), params, questionnaire.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
    }

    @PostMapping(path = "visualize-queen-telephone/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI CATI Queen from JSON serialized Pogues entity", description = "Get visualization URI CATI Queen from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeCatiQueenFromBody(
            @RequestBody String request,
            @PathVariable(value = "questionnaire") String questionnaireName,
            @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CATI");
        params.put("needDeref", ref);
        params.put("nomenclatureIds", suggesterVisuService.getNomenclatureIdsFromQuestionnaire(request));
        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(string2InputStream(request))
                .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                .map(poguesJSONToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                .transform();
        uri = lunaticJSONToUriQueen.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
    }

    @PostMapping(path = "visualize-queen/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI CAPI Queen from JSON serialized Pogues entity", description = "Get visualization URI CAPI Queen from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeQueenFromBody(
            @RequestBody String request,
            @PathVariable(value = "questionnaire") String questionnaireName,
            @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("mode", "CAPI");
        params.put("needDeref", ref);
        params.put("nomenclatureIds", suggesterVisuService.getNomenclatureIdsFromQuestionnaire(request));
        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)))
                .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                .map(poguesJSONToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                .transform();
        uri = lunaticJSONToUriQueen.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
    }

    @PostMapping(path = "visualize-stromae-v2/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI Stromae V2 from JSON serialized Pogues entity", description = "Get visualization URI Stromae V2 from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeStromaeV2FromBody(
            @RequestBody String request,
            @PathVariable(value = "questionnaire") String questionnaireName,
            @RequestParam(name = "references", defaultValue = "false") Boolean ref) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("questionnaire", questionnaireName.toLowerCase());
        params.put("needDeref", ref);
        params.put("mode", "CAWI");
        params.put("nomenclatureIds", suggesterVisuService.getNomenclatureIdsFromQuestionnaire(request));

        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(string2InputStream(request))
                .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                .map(poguesJSONToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                .transform();

        uri = lunaticJSONToUriStromaeV2.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());
    }

    @PostMapping(path = "visualize-stromae-v3/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI Stromae V3 from JSON serialized Pogues entity", description = "Get visualization URI Stromae V3 from JSON serialized Pogues entity")
    public ResponseEntity<String> visualizeStromaeV3FromBody(
            @RequestBody String request,
            @PathVariable(value = "questionnaire") String questionnaireName,
            @RequestParam(name = "references", defaultValue = "false") Boolean ref,
            @RequestParam(defaultValue = "DEFAULT") EnoContext context) throws Exception {

        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("questionnaire", questionnaireName.toLowerCase());
        params.put("needDeref", ref);
        params.put("mode", "CAWI");
        params.put("nomenclatureIds", suggesterVisuService.getNomenclatureIdsFromQuestionnaire(request));
        params.put("dsfr", true);
        params.put("context", context);
        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(string2InputStream(request))
                .map(jsonToJsonDeref::transform, params, questionnaireName.toLowerCase())
                .map(poguesJSONToLunaticJSON::transform, params, questionnaireName.toLowerCase())
                .transform();
        uri = lunaticJSONToUriStromaeV3.transform(output2Input(outputStream), params, questionnaireName.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());

    }

    @PostMapping(path = "visualize-from-ddi/{dataCollection}/{questionnaire}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get visualization URI from DDI questionnaire", description = "dataCollection MUST refer to the name attribute owned by the nested DataCollectionObject")
    public ResponseEntity<String> visualizeFromDDIBody(
            @RequestBody String request,
            @PathVariable(value = "dataCollection") String dataCollection,
            @PathVariable(value = "questionnaire") String questionnaire) throws Exception {
        PipeLine pipeline = new PipeLine();
        Map<String, Object> params = new HashMap<>();
        params.put("dataCollection", dataCollection.toLowerCase());
        params.put("questionnaire", questionnaire.toLowerCase());
        URI uri;
        ByteArrayOutputStream outputStream = pipeline.from(string2InputStream(request))
                .map(ddiToXForm::transform, params, questionnaire.toLowerCase())
                .transform();
        uri = xformToUri.transform(output2Input(outputStream), params, questionnaire.toLowerCase());
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_PLAIN).body(uri.toString());

    }

    @PostMapping(path = "xform2uri/{dataCollection}/{questionnaire}", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Get Pogues visualization URI From Pogues XForm document", description = "Returns the vizualisation URI of a form that was generated from XForm description found in body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Error") })
    public String xForm2URI(
            @RequestBody String questXforms,
            @PathVariable(value = "dataCollection") String dataCollection,
            @PathVariable(value = "questionnaire") String questionnaire) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("dataCollection", dataCollection.toLowerCase());
        params.put("questionnaire", questionnaire.toLowerCase());
        return xformToUri.transform(string2InputStream(questXforms), params, questionnaire).toString();
    }
}
