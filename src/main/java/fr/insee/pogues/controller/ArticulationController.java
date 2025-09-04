package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.mapper.ArticulationMapper;
import fr.insee.pogues.mapper.VariablesMapper;
import fr.insee.pogues.model.Articulation;
import fr.insee.pogues.model.VariableType;
import fr.insee.pogues.model.dto.articulations.ArticulationDTO;
import fr.insee.pogues.model.dto.variables.VariableDTO;
import fr.insee.pogues.service.ArticulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
 * <p>WebService class used to fetch and update the articulation of a questionnaire.</p>
 * <p>This is available only to questionnaires specified in VTL and with a roundabout.</p>
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "Articulation Controller")
@Slf4j
public class ArticulationController {

	private final ArticulationService articulationService;

	public ArticulationController(ArticulationService articulationService) {
		this.articulationService = articulationService;
	}

	@Operation(summary = "Get the articulation of a questionnaire, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire does not have a roundabout"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@GetMapping("/questionnaire/{questionnaireId}/articulation")
	public ResponseEntity<ArticulationDTO> getQuestionnaireArticulation(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		Articulation articulation = articulationService.getQuestionnaireArticulation(questionnaireId);
		ArticulationDTO articulationDTO = ArticulationMapper.toDTO(articulation);
		return ResponseEntity.status(HttpStatus.OK).body(articulationDTO);
	}

	@Operation(summary = "Get the articulation of a questionnaire's backup, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire does not have a roundabout"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@GetMapping("/questionnaire/{questionnaireId}/version/{versionId}/articulation")
	public ResponseEntity<ArticulationDTO> getQuestionnaireVersionArticulation(
			@PathVariable(value = "questionnaireId") String ignoredQuestionnaireId,
			@PathVariable(value = "versionId") UUID versionId
	) throws Exception {
		Articulation articulation = articulationService.getVersionArticulation(versionId);
		ArticulationDTO articulationDTO = ArticulationMapper.toDTO(articulation);
		return ResponseEntity.status(HttpStatus.OK).body(articulationDTO);
	}

	@Operation(summary = "Get the questionnaire's roundabout variables, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire does not have a roundabout"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@GetMapping("/questionnaire/{questionnaireId}/articulation/variables")
	public ResponseEntity<List<VariableDTO>> getQuestionnaireArticulationVariables(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		List<VariableType> variables = articulationService.getQuestionnaireArticulationVariables(questionnaireId);
		List<VariableDTO> variablesDTO = variables.stream().map(VariablesMapper::toDTO).toList();
		return ResponseEntity.status(HttpStatus.OK).body(variablesDTO);
	}

	@Operation(summary = "Update or create an articulation in a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Successfully created"),
			@ApiResponse(responseCode = "204", description = "Successfully updated"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire does not have a roundabout"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@PutMapping("/questionnaire/{questionnaireId}/articulation")
	public ResponseEntity<JsonNode> upsertQuestionnaireArticulation(
			@PathVariable(value = "questionnaireId") String questionnaireId,
			@RequestBody ArticulationDTO articulationDTO
	) throws Exception {
		Articulation articulation = ArticulationMapper.toModel(articulationDTO);
		boolean isCreated = articulationService.upsertQuestionnaireArticulation(questionnaireId, articulation);
		if (isCreated) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete the articulation of a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire does not have a roundabout"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@DeleteMapping("/questionnaire/{questionnaireId}/articulation")
	public ResponseEntity<JsonNode> deleteQuestionnaireArticulation(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		articulationService.deleteQuestionnaireArticulation(questionnaireId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
