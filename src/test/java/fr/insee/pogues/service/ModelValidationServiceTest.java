package fr.insee.pogues.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelValidationServiceTest {

    @Test
    @DisplayName("Call transform on valid questionnaire, should be unchanged.")
    void transformValid() throws Exception {
        String validPoguesJson = """
                {
                  "id": "foo-questionnaire"
                }"""; // "valid" according to the validation service
        ByteArrayOutputStream outputStream = new ModelValidationService().transform(
                new ByteArrayInputStream(validPoguesJson.getBytes()), null, null);
        assertEquals(validPoguesJson, outputStream.toString());
    }

}
