package fr.insee.pogues.controllers;

import fr.insee.pogues.controller.QuestionnaireMetadataController;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.service.QuestionnaireMetadataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QuestionnaireMetadataControllerTest {

    private final QuestionnaireMetadataService metadataService = mock(QuestionnaireMetadataService.class);
    private final QuestionnaireMetadataController controller = new QuestionnaireMetadataController(metadataService);

    @Test
    void shouldReturnZipResponseEntity() {
        // Given
        String poguesId = "test-id";

        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(1);
            os.write("fake zip content".getBytes());
            return null;
        }).when(metadataService).generateZip(ArgumentMatchers.eq(poguesId), ArgumentMatchers.any(OutputStream.class));

        // When
        ResponseEntity<byte[]> response = controller.getMetadataZip(poguesId);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=\"test-id-metadata.zip\"",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("application/octet-stream",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));

        byte[] output =  response.getBody();
        assertEquals("fake zip content", new String(output));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenZipGenerationFails() throws PoguesException {
        // Given
        String poguesId = "error-id";

        doThrow(new PoguesException(500, "Generation failed", null))
                .when(metadataService).generateZip(ArgumentMatchers.eq(poguesId), ArgumentMatchers.any(OutputStream.class));

        // When & Then
        PoguesException thrown = Assertions.assertThrows(
                PoguesException.class,
                () -> writeMetadataZipToOutputStream(poguesId)
        );

        assertEquals("Generation failed", thrown.getMessage());
    }

    private void writeMetadataZipToOutputStream(String poguesId) {
        controller.getMetadataZip(poguesId).getBody();
    }

    @Test
    void shouldHandleEmptyId() {
        // Given
        String poguesId = "";

        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.getMetadataZip(poguesId));
    }

    @Test
    void shouldHandleNullId() {
        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> controller.getMetadataZip(null));
    }
}

