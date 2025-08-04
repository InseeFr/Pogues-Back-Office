package fr.insee.pogues.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.service.VariableService;
import fr.insee.pogues.webservice.model.dtd.variables.Variable;
import fr.insee.pogues.webservice.model.dtd.variables.VariableTypeEnum;
import fr.insee.pogues.webservice.rest.VariableController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VariableController.class)
public class VariableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VariableService variableService;

    @Test
    @DisplayName("Should fetch questionnaires variables")
    void getQuestionnaireVariables_success() throws Exception {
        // Given a questionnaire with variables
        List<Variable> expected = List.of(new Variable("id", "name", "description", VariableTypeEnum.COLLECTED, "", ""));
        Mockito.when(variableService.getQuestionnaireVariables("my-q-id")).thenReturn(expected);
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(expected);

        // When we fetch the questionnaire variables
        mockMvc.perform(get("/api/persistence/questionnaire/{questionnaireId}/variables", "my-q-id")
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
        mockMvc.perform(get("/questionnaire/{questionnaireId}/variables", "my-q-id")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

}
