package fr.insee.pogues.controller;

import fr.insee.pogues.service.QuestionnaireMetadataService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
}

