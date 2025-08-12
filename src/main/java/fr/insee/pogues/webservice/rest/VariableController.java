package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.model.VariableType;
import fr.insee.pogues.service.VariableService;
import fr.insee.pogues.utils.VariablesConverter;
import fr.insee.pogues.webservice.model.dtd.variables.VariableDTO;
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
 * WebService class used to fetch and update the variables of a questionnaire.
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "Variable Controller")
@Slf4j
public class VariableController {

	private final VariableService variableService;

	public VariableController(VariableService variableService) {
		this.variableService = variableService;
	}

	@Operation(summary = "Get the variables of a questionnaire, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found") })
	@GetMapping("/questionnaire/{questionnaireId}/variables")
	public ResponseEntity<List<VariableDTO>> getQuestionnaireVariables(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		List<VariableType> variables = variableService.getQuestionnaireVariables(questionnaireId);
		List<VariableDTO> variablesDTO = variables.stream().map(VariablesConverter::toDTO).toList();
		return ResponseEntity.status(HttpStatus.OK).body(variablesDTO);
	}

	@Operation(summary = "Get the variables from a questionnaire's backup",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	@GetMapping("/questionnaire/{questionnaireId}/version/{versionId}/variables")
	public ResponseEntity<List<VariableDTO>> getQuestionnaireVersionVariables(
			@PathVariable(value = "questionnaireId") String ignoredQuestionnaireId,
			@PathVariable(value = "versionId") UUID versionId
	) throws Exception {
		List<VariableType> variables = variableService.getVersionVariables(versionId);
		List<VariableDTO> variablesDTO = variables.stream().map(VariablesConverter::toDTO).toList();
		return ResponseEntity.status(HttpStatus.OK).body(variablesDTO);
	}

	@Operation(summary = "Update or create a variable in a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully updated"),
			@ApiResponse(responseCode = "201", description = "Successfully created"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	@PostMapping("/questionnaire/{questionnaireId}/variable")
	public ResponseEntity<List<VariableDTO>> upsertQuestionnaireVariable(
			@PathVariable(value = "questionnaireId") String questionnaireId,
			@RequestBody VariableDTO variableDTO
	) throws Exception {
		VariableType variable = VariablesConverter.toModel(variableDTO);
		boolean isCreated = variableService.upsertQuestionnaireVariable(questionnaireId, variable);
		if (isCreated) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Delete the variable from a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Success"),
			@ApiResponse(responseCode = "400", description = "Variable is in use"),
			@ApiResponse(responseCode = "404", description = "Not found") })
	@DeleteMapping("/questionnaire/{questionnaireId}/variable/{variableId}")
	public ResponseEntity<JsonNode> deleteQuestionnaireVariable(
			@PathVariable(value = "questionnaireId") String questionnaireId,
			@PathVariable(value = "variableId") String variableId
	) throws Exception {
		variableService.deleteQuestionnaireVariable(questionnaireId, variableId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
