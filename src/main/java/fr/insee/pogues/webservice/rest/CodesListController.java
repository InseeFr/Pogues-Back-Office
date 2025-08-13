package fr.insee.pogues.webservice.rest;

import fr.insee.pogues.service.CodesListService;
import fr.insee.pogues.webservice.error.ApiMessage;
import fr.insee.pogues.webservice.error.CodesListMessage;
import fr.insee.pogues.webservice.model.dto.codelists.CodesList;
import fr.insee.pogues.webservice.model.dto.codelists.ExtendedCodesList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * WebService class used to fetch and update the codes lists of a questionnaire.
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "4. CodesList Controller")
@Slf4j
public class CodesListController {

    private final CodesListService codesListService;

    public CodesListController(CodesListService codesListService){
        this.codesListService = codesListService;
    }

    @Operation(summary = "Get codes lists of a questionnaire",
            responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", array = @ArraySchema( schema = @Schema(implementation = ExtendedCodesList.class)))}),
            @ApiResponse(responseCode = "404", description = "Questionnaire not found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessage.class)) }) })
    @GetMapping("/questionnaire/{questionnaireId}/codes-lists")
    public ResponseEntity<List<ExtendedCodesList>> getQuestionnaireCodesLists(
            @PathVariable(value = "questionnaireId") String questionnaireId
    ) throws Exception {
        List<ExtendedCodesList> codesLists = codesListService.getCodesListsDTDByQuestionnaireId(questionnaireId);
        return ResponseEntity.status(HttpStatus.OK).body(codesLists);
    }

    @Operation(summary = "Get the codes lists from a questionnaire's backup",
            responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json",array = @ArraySchema( schema = @Schema(implementation = ExtendedCodesList.class)))}),
            @ApiResponse(responseCode = "404", description = "Version not found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessage.class)) }) })
    @GetMapping("/questionnaire/{questionnaireId}/version/{versionId}/codes-lists")
    public ResponseEntity<List<ExtendedCodesList>> getQuestionnaireVersionCodesLists(
            @PathVariable(value = "questionnaireId") String ignoredQuestionnaireId,
            @PathVariable(value = "versionId") UUID versionId
    ) throws Exception {
        List<ExtendedCodesList> codesLists = codesListService.getCodesListsDTDByVersionId(versionId);
        return ResponseEntity.status(HttpStatus.OK).body(codesLists);
    }

    @Operation(summary = "Update or create codes list in a questionnaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "404", description = "Not found") })
    @PutMapping("/questionnaire/{questionnaireId}/codes-list/{codesListId}")
    public ResponseEntity<Object> upsertQuestionnaireCodesList(
            @PathVariable(value = "questionnaireId") String questionnaireId,
            @PathVariable(value = "codesListId") String codesListId,
            @RequestBody CodesList codesList
    ) throws Exception {
        List<String> updatedQuestionIds = codesListService.updateOrAddCodeListToQuestionnaire(questionnaireId, codesListId, codesList);
        if (updatedQuestionIds != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedQuestionIds);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "Delete the codes list from a questionnaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted"),
            @ApiResponse( responseCode = "400", description = "Codes list is used in questions", content = { @Content( mediaType = "application/json", schema = @Schema(implementation = CodesListMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found") })
    @DeleteMapping("/questionnaire/{questionnaireId}/codes-list/{codesListId}")
    public ResponseEntity<Object> deleteQuestionnaireCodesList(
            @PathVariable(value = "questionnaireId") String questionnaireId,
            @PathVariable(value = "codesListId") String codesListId
    ) throws Exception {
        codesListService.deleteCodeListOfQuestionnaireById(questionnaireId, codesListId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
