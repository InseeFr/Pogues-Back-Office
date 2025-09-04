package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.exception.QuestionnaireNotFoundException;
import fr.insee.pogues.exception.VersionNotFoundException;
import fr.insee.pogues.model.*;
import fr.insee.pogues.model.dto.multimode.MultimodeDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeItemDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleDTO;
import fr.insee.pogues.model.dto.multimode.MultimodeRuleNameDTOEnum;
import fr.insee.pogues.service.MultimodeService;
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

@WebMvcTest(MultimodeController.class)
class MultimodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MultimodeService multimodeService;

    @Test
    @DisplayName("Should fetch questionnaire multimode")
    void getQuestionnaireMultimode_success() throws Exception {
        // Given a questionnaire with multimode
        Multimode multimode = new Multimode();
        Rule ruleQ = new Rule();
        ruleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleQ.setValue("nvl(HABITEZ_VOUS_ICI, true)");
        ruleQ.setType(ValueTypeEnum.VTL);
        Rules rulesQ = new Rules();
        rulesQ.getRules().add(ruleQ);
        multimode.setQuestionnaire(rulesQ);
        Rules rulesL = new Rules();
        multimode.setLeaf(rulesL);

        Mockito.when(multimodeService.getQuestionnaireMultimode("my-q-id")).thenReturn(multimode);
        String expectedJSON = "{\"questionnaire\":{\"rules\":[{\"name\":\"IS_MOVED\",\"value\":\"nvl(HABITEZ_VOUS_ICI, true)\"}]},\"leaf\":{\"rules\":[]}}";

        // When we fetch the questionnaire multimode
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/multimode")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the multimode is returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should return an empty object when multimode is empty")
    void getQuestionnaireMultimode_success_empty() throws Exception {
        // Given a questionnaire without multimode
        Mockito.when(multimodeService.getQuestionnaireMultimode("my-q-id")).thenReturn(null);
        String expectedJSON = "{}";

        // When we fetch the questionnaire multimode
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/multimode")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the multimode is returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch multimode from a questionnaire that does not exist")
    void getQuestionnaireMultimode_error_notFound() throws Exception {
        // Given no questionnaire
        Mockito.when(multimodeService.getQuestionnaireMultimode("my-q-id"))
                .thenThrow(new QuestionnaireNotFoundException("Questionnaire not found"));

        // When we fetch the questionnaire multimode
        mockMvc.perform(get("/api/persistence/questionnaire/my-q-id/multimode")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should fetch questionnaire's backup multimode")
    void getVersionMultimode_success() throws Exception {
        // Given a questionnaire's version with multimode
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Multimode multimode = new Multimode();
        Rule ruleQ = new Rule();
        ruleQ.setName(MultimodeRuleNameEnum.IS_MOVED);
        ruleQ.setValue("nvl(HABITEZ_VOUS_ICI, true)");
        ruleQ.setType(ValueTypeEnum.VTL);
        Rules rulesQ = new Rules();
        rulesQ.getRules().add(ruleQ);
        multimode.setQuestionnaire(rulesQ);
        Rules rulesL = new Rules();
        multimode.setLeaf(rulesL);

        Mockito.when(multimodeService.getVersionMultimode(versionId)).thenReturn(multimode);
        String expectedJSON = "{\"questionnaire\":{\"rules\":[{\"name\":\"IS_MOVED\",\"value\":\"nvl(HABITEZ_VOUS_ICI, true)\"}]},\"leaf\":{\"rules\":[]}}";

        // When we fetch the questionnaire multimode
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/multimode", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the multimode are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should trigger an error when we try to fetch multimode from a questionnaire's backup that does not exist")
    void getVersionMultimode_error_notFound() throws Exception {
        // Given no questionnaire's version
        UUID versionId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Mockito.when(multimodeService.getVersionMultimode(versionId))
                .thenThrow(new VersionNotFoundException("Version not found"));

        // When we fetch the questionnaire multimode
        mockMvc.perform(get(String.format("/api/persistence/questionnaire/my-q-id/version/%s/multimode", versionId))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 404
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should insert questionnaire multimode")
    void upsertQuestionnaireMultimode_success_created() throws Exception {
        // Given a multimode
        MultimodeDTO multimode = new MultimodeDTO(
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(HABITEZ_VOUS_ICI, true)")
            )),
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(PRENOM_HABITE_PLUS_LA, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(fzqfqzgjh, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_SPLIT, "nvl(PRENOM_HABITE_PLUS_LA, false)")
            ))
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(multimode);
        Mockito.when(multimodeService.upsertQuestionnaireMultimode(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getQuestionnaire().getRules().size(), multimode.getQuestionnaire().getRules().size()))))
                .thenReturn(true);

        // When we insert the multimode in the questionnaire
        mockMvc.perform(put("/api/persistence/questionnaire/my-q-id/multimode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 201 and the multimode is added to the questionnaire
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should update questionnaire multimode")
    void upsertQuestionnaireMultimode_success_updated() throws Exception {
        // Given a questionnaire with a multimode
        MultimodeDTO multimode = new MultimodeDTO(
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(HABITEZ_VOUS_ICI, true)")
            )),
            new MultimodeItemDTO(List.of(
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(PRENOM_HABITE_PLUS_LA, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_MOVED, "nvl(fzqfqzgjh, false)"),
                new MultimodeRuleDTO(MultimodeRuleNameDTOEnum.IS_SPLIT, "nvl(PRENOM_HABITE_PLUS_LA, false)")
            ))
        );
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedJSON = objectMapper.writeValueAsString(multimode);
        Mockito.when(multimodeService.upsertQuestionnaireMultimode(eq("my-q-id"), argThat(arg -> Objects.equals(arg.getQuestionnaire().getRules().size(), multimode.getQuestionnaire().getRules().size()))))
                .thenReturn(false);

        // When we update the questionnaire multimode
        mockMvc.perform(put("/api/persistence/questionnaire/my-q-id/multimode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedJSON)
                        .characterEncoding("utf-8")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 204 and the multimode is added to the questionnaire
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should delete questionnaire multimode")
    void deleteQuestionnaireMultimode_success() throws Exception {
        // Given a questionnaire with a multimode
        Mockito.doNothing().when(multimodeService).deleteQuestionnaireMultimode("my-q-id");

        // When we delete the questionnaire multimode
        mockMvc.perform(delete("/api/persistence/questionnaire/my-q-id/multimode")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 204 and the multimode is deleted from the questionnaire
                .andExpect(status().isNoContent());
    }
}
