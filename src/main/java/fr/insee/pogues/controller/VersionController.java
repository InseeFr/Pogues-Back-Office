package fr.insee.pogues.controller;

import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.persistence.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/persistence")
@Tag(name = "6. Version of questionnaire Controller")
@Slf4j
public class VersionController {

    @Autowired
    private VersionService versionService;

    @GetMapping("questionnaire/{poguesId}/versions")
    @Operation(
            operationId  = "getVersionsByQuestionnaireId",
            summary = "Get all versions for questionnaire",
            description = "Get all versions for questionnaire according its id, without data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public List<Version> getVersionsByQuestionnaireId(
            @PathVariable(value = "poguesId") String poguesId,
            @RequestParam(name = "withData", required = false, defaultValue = "false")  Boolean withData) throws Exception {
        return versionService.getVersionsByQuestionnaireId(poguesId, withData);
    }

    @GetMapping("questionnaire/{poguesId}/version/last")
    @Operation(
            operationId  = "getLastVersionByQuestionnaireId",
            summary = "Get last version for questionnaire",
            description = "Get last version for questionnaire according its id, without data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public Version getLastVersionByQuestionnaireId(
            @PathVariable(value = "poguesId") String poguesId,
            @RequestParam(name = "withData", required = false, defaultValue = "false") Boolean withData) throws Exception {
        return versionService.getLastVersionByQuestionnaireId(poguesId, withData);
    }

    @PostMapping("questionnaire/restore/{versionId}")
    @Operation(
            operationId  = "restoreVersionByVersion",
            summary = "Restore an old version according to its id",
            description = "Restore an old version according to its id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public void restoreVersionByVersionId(
            @PathVariable(value = "versionId") UUID versionId) throws Exception {
        versionService.restoreVersion(versionId);
    }

    @GetMapping("version/{versionId}")
    @Operation(
            operationId  = "getVersionByVersionId",
            summary = "Get version by its id",
            description = "Get version by its id, with or without data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public Version getVersionByVersionId(
            @PathVariable(value = "versionId") UUID versionId,
            @RequestParam(name = "withData", required = false, defaultValue = "false") Boolean withData) throws Exception {
        return versionService.getVersionByVersionId(versionId, withData);
    }

    @DeleteMapping("questionnaire/{poguesId}/versions")
    @Operation(
            operationId  = "deleteVersionsByQuestionnaireId",
            summary = "Delete versions by pogues_id",
            description = "Delete all versions according to pogues_id"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public void deleteVersionsByQuestionnaireId(String poguesId) throws Exception {
        versionService.deleteVersionsByQuestionnaireId(poguesId);
    }
}
