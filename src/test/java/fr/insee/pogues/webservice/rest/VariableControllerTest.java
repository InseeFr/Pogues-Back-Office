package fr.insee.pogues.webservice.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.VariableNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.service.VariableService;
import fr.insee.pogues.webservice.model.dto.variables.VariableDTO;
import fr.insee.pogues.webservice.model.dto.variables.VariableDTODatatype;
import fr.insee.pogues.webservice.model.dto.variables.VariableDTODatatypeTypeEnum;
import fr.insee.pogues.webservice.model.dto.variables.VariableDTOTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VariableController.class)
class VariableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VariableService variableService;

    @Test
    @DisplayName("Should fetch questionnaires variables")
    void getQuestionnaireVariables_success() throws Exception {
        // Given a questionnaire with variables
        VariableType variable = new CollectedVariableType();
        variable.setId("id");
        variable.setName("name");
        variable.setLabel("description");
        BooleanDatatypeType datatype = new BooleanDatatypeType();
        datatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variable.setDatatype(datatype);
        Mockito.when(variableService.getQuestionnaireVariables("my-q-id")).thenReturn(List.of(variable));
        String expectedJSON = "[{\"id\":\"id\",\"name\":\"name\",\"description\":\"description\",\"type\":\"COLLECTED\",\"datatype\":{\"typeName\":\"BOOLEAN\"}}]";

        // When we fetch the questionnaire variables
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/variables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the variables are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch variables from a questionnaire that does not exist")
    void getQuestionnaireVariables_error_notFound() throws Exception {
        // Given no questionnaire
        Mockito.when(variableService.getQuestionnaireVariables("my-q-id"))
                .thenThrow(new PoguesException(404, "Questionnaire not found", ""));

        // When we fetch the questionnaire variables
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/variables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fetch questionnaires' backup variables")
    void getQuestionnaireVersionVariables_success() throws Exception {
        // Given a questionnaire's version with variables
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        VariableType variable = new CollectedVariableType();
        variable.setId("id");
        variable.setName("name");
        variable.setLabel("description");
        BooleanDatatypeType datatype = new BooleanDatatypeType();
        datatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variable.setDatatype(datatype);
        Mockito.when(variableService.getVersionVariables(versionId)).thenReturn(List.of(variable));
        String expectedJSON = "[{\"id\":\"id\",\"name\":\"name\",\"description\":\"description\",\"type\":\"COLLECTED\",\"datatype\":{\"typeName\":\"BOOLEAN\"}}]";

        // When we fetch the questionnaire variables
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/variables", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the variables are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch variables from a questionnaire' backup that does not exist")
    void getQuestionnaireVersionVariables_error_notFound() throws Exception {
        // Given no questionnaire's version
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Mockito.when(variableService.getVersionVariables(versionId))
                .thenThrow(new PoguesException(404, "Version not found", ""));

        // When we fetch the questionnaire variables
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/variables", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should insert questionnaire variable")
    void upsertQuestionnaireVariable_success_created() throws Exception {
        // Given a variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variable = new VariableDTO("id", "name", "description", VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(variable);
        Mockito.when(variableService.upsertQuestionnaireVariable(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getId(), variable.getId()))))
                .thenReturn(true);

        // When we insert the variable in the questionnaire
        mockMvc.perform(post("/api/persistence/questionnaire/my-q-id/variable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 201 and the variable is added to the questionnaire
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should update questionnaire variable")
    void upsertQuestionnaireVariable_success_updated() throws Exception {
        // Given a questionnaire with a variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variable = new VariableDTO("id", "name", "description", VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(variable);
        Mockito.when(variableService.upsertQuestionnaireVariable(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getId(), variable.getId()))))
                .thenReturn(false);

        // When we update the variable in the questionnaire
        mockMvc.perform(post("/api/persistence/questionnaire/my-q-id/variable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the variable is updated in the questionnaire
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should trigger an error when we try to update a variable from a questionnaire that does not exist")
    void upsertQuestionnaireVariable_error_notFound() throws Exception {
        // Given a variable
        VariableDTODatatype datatypeDTO = new VariableDTODatatype(VariableDTODatatypeTypeEnum.BOOLEAN, null, null, null, null, null, null, null);
        VariableDTO variable = new VariableDTO("id", "name", "description", VariableDTOTypeEnum.COLLECTED, null, null, datatypeDTO);
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(variable);
        Mockito.when(variableService.upsertQuestionnaireVariable(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getId(), variable.getId()))))
                .thenThrow(new PoguesException(404, "Questionnaire not found", ""));

        // When we try to insert the variable in a questionnaire which does not exist
        mockMvc.perform(post("/api/persistence/questionnaire/my-q-id/variable")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete questionnaire variable")
    void deleteQuestionnaireVariable_success() throws Exception {
        // Given a questionnaire with a variable
        Mockito.doNothing().when(variableService).deleteQuestionnaireVariable("my-q-id", "my-var-id");

        // When we delete the questionnaire variable
        mockMvc.perform(delete("/api/persistence/questionnaire/my-q-id/variable/my-var-id")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the variables are returned
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should trigger an error when we try to delete a variable from a questionnaire that does not exist")
    void deleteQuestionnaireVariable_error_questionnaireNotFound() throws Exception {
        // Given a questionnaire that does not exist
        Mockito.doThrow(new PoguesException(404, "Questionnaire not found", ""))
                .when(variableService).deleteQuestionnaireVariable("my-q-id", "my-var-id");

        // When we delete the questionnaire variable
        mockMvc.perform(delete("/api/persistence/questionnaire/my-q-id/variable/my-var-id")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should trigger an error when we try to delete a variable that does not exist")
    void deleteQuestionnaireVariable_error_variableNotFound() throws Exception {
        // Given a variable that does not exist in a questionnaire*
        Mockito.doThrow(new VariableNotFoundException("Variable not found"))
                .when(variableService).deleteQuestionnaireVariable("my-q-id", "my-var-id");

        // When we delete the questionnaire variable
        mockMvc.perform(delete("/api/persistence/questionnaire/my-q-id/variable/my-var-id")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

}
