package fr.insee.pogues.controller;

import fr.insee.pogues.service.NomenclatureService;
import fr.insee.pogues.webservice.error.ApiMessage;
import fr.insee.pogues.webservice.model.dtd.nomenclatures.ExtendedNomenclature;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questionnaires")
@Tag(name = "5. Nomenclature Controller")
@Slf4j
public class NomenclatureController {

    private NomenclatureService nomenclatureService;

    public NomenclatureController(NomenclatureService nomenclatureService){
        this.nomenclatureService = nomenclatureService;
    }

    @GetMapping("{questionnaireId}/nomenclatures")
    @Operation(
            operationId  = "getNomenclaturesInQuestionnaire",
            summary = "Get nomenclatures in questionnaire",
            description = "Get all nomenclatures for questionnaire of id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200 ",
                    description = "Success",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(
                                    schema = @Schema(implementation = ExtendedNomenclature.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiMessage.class)) })
    })
    public ResponseEntity<List<ExtendedNomenclature>> getNomenclaturesInQuestionnaire(
            @PathVariable(value = "questionnaireId") String questionnaireId) throws Exception {
        List<ExtendedNomenclature> nomenclatures = nomenclatureService.getNomenclaturesDTDByQuestionnaireId(questionnaireId);
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatures);
    }

    @GetMapping("{questionnaireId}/version/{versionId}/nomenclatures")
    @Operation(
            operationId  = "getNomenclaturesInVersionOfQuestionnaire",
            summary = "Get nomenclatures in backup od questionnaire",
            description = "Get all nomenclatures for questionnaire of id and backup of id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200 ",
                    description = "Success",
                    content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(
                                    schema = @Schema(implementation = ExtendedNomenclature.class)))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiMessage.class)) })
    })
    public ResponseEntity<List<ExtendedNomenclature>> getNomenclaturesInVersionOfQuestionnaire(
            @PathVariable(value = "questionnaireId") String questionnaireId,
            @PathVariable(value = "versionId") UUID versionId) throws Exception {
        log.info("Get nomenclatures of backup (id: {}) of questionnaire {}.", versionId, questionnaireId);
        List<ExtendedNomenclature> nomenclatures = nomenclatureService.getNomenclaturesDTDByVersionId(versionId);
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatures);
    }
}
