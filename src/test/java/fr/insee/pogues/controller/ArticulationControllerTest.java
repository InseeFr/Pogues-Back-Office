package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.exception.QuestionnaireNotFoundException;
import fr.insee.pogues.exception.VersionNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.articulations.ArticulationDTO;
import fr.insee.pogues.model.dto.articulations.ArticulationItemDTO;
import fr.insee.pogues.service.ArticulationService;
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

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArticulationController.class)
class ArticulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArticulationService articulationService;

    @Test
    @DisplayName("Should fetch questionnaire articulation")
    void getQuestionnaireArticulation_success() throws Exception {
        // Given a questionnaire with articulation
        Articulation articulation = new Articulation();
        Item item = new Item();
        item.setLabel("my label");
        item.setValue("my value");
        item.setType(ValueTypeEnum.VTL);
        articulation.getItems().add(item);

        Mockito.when(articulationService.getQuestionnaireArticulation("my-q-id")).thenReturn(articulation);
        String expectedJSON = "{\"items\":[{\"label\":\"my label\",\"value\":\"my value\"}]}";

        // When we fetch the questionnaire articulation
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/articulation")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the articulation are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch articulation from a questionnaire that does not exist")
    void getQuestionnaireArticulation_error_notFound() throws Exception {
        // Given no questionnaire
        Mockito.when(articulationService.getQuestionnaireArticulation("my-q-id"))
                .thenThrow(new QuestionnaireNotFoundException("Questionnaire not found"));

        // When we fetch the questionnaire variables
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/articulation")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fetch questionnaire's backup articulation")
    void getVersionArticulation_success() throws Exception {
        // Given a questionnaire's version with articulation
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Articulation articulation = new Articulation();
        Item item = new Item();
        item.setLabel("my label");
        item.setValue("my value");
        item.setType(ValueTypeEnum.VTL);
        articulation.getItems().add(item);

        Mockito.when(articulationService.getVersionArticulation(versionId)).thenReturn(articulation);
        String expectedJSON = "{\"items\":[{\"label\":\"my label\",\"value\":\"my value\"}]}";

        // When we fetch the questionnaire articulation
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/articulation", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the articulation are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch variables from a questionnaire's backup that does not exist")
    void getVersionArticulation_error_notFound() throws Exception {
        // Given no questionnaire's version
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Mockito.when(articulationService.getVersionArticulation(versionId))
                .thenThrow(new VersionNotFoundException("Version not found"));

        // When we fetch the questionnaire articulation
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/articulation", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fetch questionnaire variables available for articulation")
    void getQuestionnaireArticulationVariables_success() throws Exception {
        // Given a questionnaire with a roundabout and associated variable
        VariableType variable = new CollectedVariableType();
        variable.setId("id");
        variable.setName("name");
        variable.setLabel("description");
        BooleanDatatypeType datatype = new BooleanDatatypeType();
        datatype.setTypeName(DatatypeTypeEnum.BOOLEAN);
        variable.setDatatype(datatype);
        Mockito.when(articulationService.getQuestionnaireArticulationVariables("my-q-id")).thenReturn(List.of(variable));
        String expectedJSON = "[{\"id\":\"id\",\"name\":\"name\",\"description\":\"description\",\"type\":\"COLLECTED\",\"datatype\":{\"typeName\":\"BOOLEAN\"}}]";

        // When we fetch the questionnaire variables available for articulation
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/articulation/variables")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the variables are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should insert questionnaire articulation")
    void upsertQuestionnaireVariable_success_created() throws Exception {
        // Given a variable
        ArticulationDTO articulation = new ArticulationDTO(List.of(
                new ArticulationItemDTO("my label", "my value"),
                new ArticulationItemDTO("my other label", "my other value")
        ));
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(articulation);
        Mockito.when(articulationService.upsertQuestionnaireArticulation(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getItems().size(), articulation.getItems().size()))))
                .thenReturn(true);

        // When we insert the variable in the questionnaire
        mockMvc.perform(put("/api/persistence/questionnaire/my-q-id/articulation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 201 and the articulation is added to the questionnaire
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should update questionnaire articulation")
    void upsertQuestionnaireVariable_success_updated() throws Exception {
        // Given a questionnaire with an articulation
        ArticulationDTO articulation = new ArticulationDTO(List.of(
                new ArticulationItemDTO("my label", "my value"),
                new ArticulationItemDTO("my other label", "my other value")
        ));
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(articulation);
        Mockito.when(articulationService.upsertQuestionnaireArticulation(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getItems().size(), articulation.getItems().size()))))
                .thenReturn(false);

        // When we update the questionnaire articulation
        mockMvc.perform(put("/api/persistence/questionnaire/my-q-id/articulation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 204 and the articulation is added to the questionnaire
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should delete questionnaire articulation")
    void deleteQuestionnaireArticulation_success() throws Exception {
        // Given a questionnaire with an articulation
        Mockito.doNothing().when(articulationService).deleteQuestionnaireArticulation("my-q-id");

        // When we delete the questionnaire articulation
        mockMvc.perform(delete("/api/persistence/questionnaire/my-q-id/articulation")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 204 and the articulation is deleted from the questionnaire
                .andExpect(status().isNoContent());
    }
}
