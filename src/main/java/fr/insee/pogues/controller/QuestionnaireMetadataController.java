package fr.insee.pogues.controller;

import fr.insee.pogues.exception.QuestionnaireMetadataException;
import fr.insee.pogues.exception.QuestionnaireMetadataRuntimeException;
import fr.insee.pogues.service.QuestionnaireMetadataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/api/questionnaire")
@Tag(name = "3. Questionnaire Metadata Controller")
@Slf4j
public class QuestionnaireMetadataController {

    private final QuestionnaireMetadataService metadataService;

    public QuestionnaireMetadataController(QuestionnaireMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    /**
     * Endpoint to generate and download a ZIP archive containing metadata
     * (JSON + DDI XML) of the questionnaire identified by its Pogues ID.
     *
     * @param poguesId Identifier of the questionnaire
     * @return ZIP file as a downloadable response
     */
    @GetMapping("/{poguesId}/metadata")
    public ResponseEntity<StreamingResponseBody> getMetadataZip(@PathVariable String poguesId) {
        StreamingResponseBody stream = outputStream ->
        {
            try {
                metadataService.generateZip(poguesId, outputStream);
            } catch (QuestionnaireMetadataException e) {
                throw new QuestionnaireMetadataRuntimeException("Failed to generate metadata ZIP", e);
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + poguesId + "-metadata.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }
}

