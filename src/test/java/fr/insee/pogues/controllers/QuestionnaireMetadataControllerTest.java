package fr.insee.pogues.controllers;

import fr.insee.pogues.controller.QuestionnaireMetadataController;
import fr.insee.pogues.exception.QuestionnaireMetadataException;
import fr.insee.pogues.exception.QuestionnaireMetadataRuntimeException;
import fr.insee.pogues.service.QuestionnaireMetadataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QuestionnaireMetadataControllerTest {

    private final QuestionnaireMetadataService metadataService = mock(QuestionnaireMetadataService.class);
    private final QuestionnaireMetadataController controller = new QuestionnaireMetadataController(metadataService);

    @Test
    void shouldReturnZipResponseEntity() throws Exception {
        // Given
        String poguesId = "test-id";

        doAnswer(invocation -> {
            OutputStream os = invocation.getArgument(1);
            os.write("fake zip content".getBytes());
            return null;
        }).when(metadataService).generateZip(ArgumentMatchers.eq(poguesId), ArgumentMatchers.any(OutputStream.class));

        // When
        ResponseEntity<StreamingResponseBody> response = controller.getMetadataZip(poguesId);

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals("attachment; filename=test-id-metadata.zip",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("application/octet-stream",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        Objects.requireNonNull(response.getBody()).writeTo(resultStream);
        assertEquals("fake zip content", resultStream.toString());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenZipGenerationFails() throws QuestionnaireMetadataException {
        // Given
        String poguesId = "error-id";

        doThrow(new QuestionnaireMetadataException(500, "Generation failed"))
                .when(metadataService).generateZip(ArgumentMatchers.eq(poguesId), ArgumentMatchers.any(OutputStream.class));

        // When & Then
        QuestionnaireMetadataRuntimeException thrown = Assertions.assertThrows(
                QuestionnaireMetadataRuntimeException.class,
                () -> writeMetadataZipToOutputStream(poguesId)
        );

        assertEquals("Failed to generate metadata ZIP", thrown.getMessage());
        assertEquals("Generation failed", thrown.getCause().getMessage());
    }

    private void writeMetadataZipToOutputStream(String poguesId) throws IOException {
        Objects.requireNonNull(controller.getMetadataZip(poguesId).getBody()).writeTo(new ByteArrayOutputStream());
    }

}

