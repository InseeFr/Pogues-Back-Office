package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.mapper.MultimodeMapper;
import fr.insee.pogues.model.Multimode;
import fr.insee.pogues.model.dto.multimode.MultimodeDTO;
import fr.insee.pogues.service.MultimodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * <p>WebService class used to fetch and update the multimode of a questionnaire.</p>
 * <p>This is available only to questionnaires specified in VTL.</p>
 */
@RestController
@RequestMapping("/api/persistence")
@Tag(name = "Multimode Controller")
@Slf4j
public class MultimodeController {

	private final MultimodeService multimodeService;

	public MultimodeController(MultimodeService multimodeService) {
		this.multimodeService = multimodeService;
	}

	@Operation(summary = "Get the multimode of a questionnaire, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@GetMapping("/questionnaire/{questionnaireId}/multimode")
	public ResponseEntity<MultimodeDTO> getQuestionnaireMultimode(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		Multimode multimode = multimodeService.getQuestionnaireMultimode(questionnaireId);
		MultimodeDTO multimodeDTO = MultimodeMapper.toDTO(multimode);
		return ResponseEntity.status(HttpStatus.OK).body(multimodeDTO);
	}

	@Operation(summary = "Get the multimode of a questionnaire's backup, used for pogues frontend",
			responses = { @ApiResponse(content = @Content(mediaType = "application/json")) })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Success"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@GetMapping("/questionnaire/{questionnaireId}/version/{versionId}/multimode")
	public ResponseEntity<MultimodeDTO> getQuestionnaireVersionMultimode(
			@PathVariable(value = "questionnaireId") String ignoredQuestionnaireId,
			@PathVariable(value = "versionId") UUID versionId
	) throws Exception {
		Multimode multimode = multimodeService.getVersionMultimode(versionId);
		MultimodeDTO multimodeDTO = MultimodeMapper.toDTO(multimode);
		return ResponseEntity.status(HttpStatus.OK).body(multimodeDTO);
	}

	@Operation(summary = "Update or create a multimode in a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Successfully created"),
			@ApiResponse(responseCode = "204", description = "Successfully updated"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@PutMapping("/questionnaire/{questionnaireId}/multimode")
	public ResponseEntity<JsonNode> upsertQuestionnaireMultimode(
			@PathVariable(value = "questionnaireId") String questionnaireId,
			@RequestBody MultimodeDTO multimodeDTO
	) throws Exception {
		Multimode multimode = MultimodeMapper.toModel(multimodeDTO);
		boolean isCreated = multimodeService.upsertQuestionnaireMultimode(questionnaireId, multimode);
		if (isCreated) {
			return ResponseEntity.status(HttpStatus.CREATED).build();
		}
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "Delete the multimode of a questionnaire")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Questionnaire not found"),
			@ApiResponse(responseCode = "422", description = "Questionnaire is not in VTL") })
	@DeleteMapping("/questionnaire/{questionnaireId}/multimode")
	public ResponseEntity<JsonNode> deleteQuestionnaireMultimode(
			@PathVariable(value = "questionnaireId") String questionnaireId
	) throws Exception {
		multimodeService.deleteQuestionnaireMultimode(questionnaireId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
