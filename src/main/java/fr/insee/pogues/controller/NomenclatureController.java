package fr.insee.pogues.controller;

import fr.insee.pogues.configuration.auth.AuthorityPrivileges;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.service.NomenclatureService;
import fr.insee.pogues.controller.error.ApiMessage;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * WebService class used to fetch the nomenclatures of a questionnaire.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "5. Nomenclature Controller")
@Slf4j
public class NomenclatureController {

    private final NomenclatureService nomenclatureService;

    public NomenclatureController(NomenclatureService nomenclatureService){
        this.nomenclatureService = nomenclatureService;
    }

    @Operation(summary = "Get the nomenclatures of a questionnaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExtendedNomenclatureDTO.class))) }),
            @ApiResponse(responseCode = "404", description = "Not found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessage.class)) }) })
    @GetMapping("/questionnaires/{questionnaireId}/nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<List<ExtendedNomenclatureDTO>> getQuestionnaireNomenclatures(
            @PathVariable(value = "questionnaireId") String questionnaireId
    ) throws Exception {
        List<ExtendedNomenclatureDTO> nomenclatures = nomenclatureService.getQuestionnaireNomenclatures(questionnaireId);
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatures);
    }

    @Operation(summary = "Get the nomenclatures from a questionnaire's backup")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ExtendedNomenclatureDTO.class))) }),
            @ApiResponse(responseCode = "404", description = "Version not found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessage.class)) }) })
    @GetMapping("/questionnaires/{questionnaireId}/version/{versionId}/nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<List<ExtendedNomenclatureDTO>> getQuestionnaireVersionNomenclatures(
            @PathVariable(value = "questionnaireId") String ignoredQuestionnaireId,
            @PathVariable(value = "versionId") UUID versionId
    ) throws Exception {
        List<ExtendedNomenclatureDTO> nomenclatures = nomenclatureService.getVersionNomenclatures(versionId);
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatures);
    }

    @Operation(summary = "Get the nomenclatures' URLs of the questionnaire (and its dependencies)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NomenclatureUrlDTO.class))) }),
            @ApiResponse(responseCode = "404", description = "Not found", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ApiMessage.class)) }) })
    @GetMapping("/questionnaires/{questionnaireId}/nomenclatures/urls")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<List<NomenclatureUrlDTO>> getQuestionnaireNomenclatureUrls(
            @PathVariable(value = "questionnaireId") String questionnaireId
    ) throws Exception {
        List<NomenclatureUrlDTO> nomenclatureUrls = nomenclatureService.getNomenclaturesUrls(questionnaireId);
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatureUrls);
    }

    @Operation(summary = "Get the nomenclatures from registry that can be used by the users in the questionnaire.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NomenclatureDTO.class)))
            }),
    })
    @GetMapping("/nomenclatures")
    @PreAuthorize(AuthorityPrivileges.HAS_USER_PRIVILEGES)
    public ResponseEntity<List<NomenclatureDTO>> getAllNomenclatures() throws Exception {
        List<NomenclatureDTO> nomenclatures = nomenclatureService.getAllNomenclatures();
        return ResponseEntity.status(HttpStatus.OK).body(nomenclatures);
    }
}
