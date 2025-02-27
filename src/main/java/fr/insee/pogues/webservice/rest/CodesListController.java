package fr.insee.pogues.webservice.rest;


import fr.insee.pogues.service.CodesListService;
import fr.insee.pogues.webservice.error.CodesListMessage;
import fr.insee.pogues.webservice.model.dtd.codeList.CodesList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/persistence")
@Tag(name = "3. CodesList Controller")
public class CodesListController {

    private CodesListService codesListService;

    @Autowired
    public CodesListController(CodesListService codesListService){
        this.codesListService = codesListService;
    }
    /**
     *
     * @param questionnaireId (id of questionnaire)
     * @param codesListId (id of codeList to delete)
     * @return
     * @throws Exception
     */
    @DeleteMapping("questionnaire/{questionnaireId}/codes-list/{codesListId}")
    @Operation(
            operationId  = "deleteCodesListInQuestionnaire",
            summary = "Delete codes list in questionnaire",
            description = "Delete codes list (with idCodesList) for questionnaire of id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success - Deleted"),
            @ApiResponse(
                    responseCode = "400",
                    description = "The codesList is required",
                    content = { @Content( mediaType = "application/json", schema = @Schema(implementation = CodesListMessage.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> deleteCodesListInQuestionnaire(
            @PathVariable(value = "questionnaireId") String questionnaireId,
            @PathVariable(value = "codesListId") String codesListId) throws Exception {
        codesListService.deleteCodeListOfQuestionnaireWithId(questionnaireId, codesListId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("questionnaire/{questionnaireId}/codes-list/{codesListId}")
    @Operation(
            operationId  = "updateOrAddCodesListInQuestionnaire",
            summary = "Update or add codes list in questionnaire",
            description = "Update codes list (with codesListId) for questionnaire of id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201 ", description = "Success - Created"),
            @ApiResponse(responseCode = "204", description = "Success - Edited"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<Object> updateOrAddCodesListInQuestionnaire(
            @PathVariable(value = "questionnaireId") String questionnaireId,
            @PathVariable(value = "codesListId") String codesListId,
            @RequestBody CodesList codesList) throws Exception {
        boolean created = codesListService.updateOrAddCodeListToQuestionnaire(questionnaireId, codesListId, codesList);
        return ResponseEntity.status(created ? HttpStatus.CREATED : HttpStatus.NO_CONTENT).build();
    }
}
