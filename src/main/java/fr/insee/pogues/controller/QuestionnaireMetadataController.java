package fr.insee.pogues.controller;

import fr.insee.pogues.service.QuestionnaireMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/questionnaire")
@Tag(name = "3. Questionnaire Metadata Controller")
@Slf4j
public class QuestionnaireMetadataController {

    private final QuestionnaireMetadataService metadataService;

    public QuestionnaireMetadataController(QuestionnaireMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    private static String getZipFileName(String poguesId){
        return String.format("pogues-metadata-%s.zip", poguesId);
    }

    /**
     * Endpoint to generate and download a ZIP archive containing metadata
     * (JSON + DDI XML) of the questionnaire identified by its Pogues ID.
     *
     * @param poguesId Identifier of the questionnaire
     * @return ZIP file as a downloadable response
     */
    @Operation(summary = "Generate and download questionnaire metadata ZIP",
            description = "Returns a ZIP archive containing the JSON and DDI XML metadata of the questionnaire identified by its Pogues ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ZIP file generated successfully"),
            @ApiResponse(responseCode = "404", description = "Questionnaire not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/{poguesId}/zip-metadata", produces = "application/zip")
    public ResponseEntity<byte[]> getMetadataZip(@PathVariable String poguesId) {
        if (poguesId == null || poguesId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pogues ID cannot be null or empty");
        }

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        metadataService.generateZip(poguesId, byteStream);
        byte[] zipBytes = byteStream.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getZipFileName(poguesId) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBytes);
    }
}

