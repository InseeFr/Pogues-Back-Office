package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fr.insee.pogues.configuration.auth.UserProvider;
import fr.insee.pogues.configuration.auth.user.User;
import fr.insee.pogues.configuration.properties.ApplicationProperties;
import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.persistence.service.QuestionnairesService;
import fr.insee.pogues.persistence.service.VariablesService;
import fr.insee.pogues.utils.suggester.SuggesterVisuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * WebService class for the Instrument Persistence
 * 
 * See the swagger documentation for this service :
 * http://inseefr.github.io/Pogues/en/remote-apis/swagger.html
 * 
 * @author I6VWID
 * 
 *         schemes: - http
 * 
 *         consumes: - application/json
 * 
 *         produces: - application/json
 *
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "3. Version of questionnaire Controller")
@Slf4j
public class VersionController {


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
            @RequestParam(name = "withData", required = false, defaultValue = "false")  Boolean withData) {
        return List.of();
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
            @RequestParam(name = "withData", required = false, defaultValue = "false") Boolean withData){
        return null;
    }

    public JsonNode getVersionDataByVersionId(String versionId){
        return null;
    }

    @GetMapping("versions/{versionId}/version/last")
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
            @PathVariable(value = "versionId") String versionId,
            @RequestParam(name = "withData", required = false, defaultValue = "false") Boolean withData){
        return null;
    }

    public void deleteVersionsByQuestionnaireId(String poguesId){
    }
}
