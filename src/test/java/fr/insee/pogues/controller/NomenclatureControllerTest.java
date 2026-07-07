package fr.insee.pogues.controller;

import fr.insee.pogues.configuration.log.LogInterceptor;
import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureUrlDTO;
import fr.insee.pogues.service.NomenclatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "testUser", roles = {"ADMIN"})
@WebMvcTest(NomenclatureController.class)
class NomenclatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NomenclatureService nomenclatureService;

    @MockitoBean
    private LogInterceptor logInterceptor;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public NomenclatureService nomenclatureService() {
            return Mockito.mock(NomenclatureService.class);
        }
    }

    @BeforeEach
    void resetMocks() {
        Mockito.reset(nomenclatureService, logInterceptor);
        Mockito.when(logInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(true);
    }

    @Test
    @DisplayName("Should fetch questionnaires nomenclatures")
    void getQuestionnaireVariables_success() throws Exception {
        // Given a questionnaire with nomenclatures
        NomenclatureDTO nomenclature = new NomenclatureDTO("id", "label", "version", "urn", null, "theme", "refYear");
        ExtendedNomenclatureDTO extendedNomenclature = new ExtendedNomenclatureDTO(nomenclature, List.of("Q1", "Q2"));
        Mockito.when(nomenclatureService.getQuestionnaireNomenclatures("my-q-id")).thenReturn(List.of(extendedNomenclature));
        String expectedJSON = "[{\"id\":\"id\",\"label\":\"label\",\"referenceYear\":\"refYear\",\"relatedQuestionNames\":[\"Q1\",\"Q2\"],\"theme\":\"theme\",\"urn\":\"urn\",\"version\":\"version\"}]";

        // When we fetch the questionnaire nomenclatures
        mockMvc.perform(get("/api/questionnaires/my-q-id/nomenclatures")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the nomenclatures are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

    @Test
    @DisplayName("Should fetch questionnaire nomenclature URLs")
    void getQuestionnaireNomenclatureUrls_success() throws Exception {
        // Given a questionnaire with nomenclatures
        NomenclatureUrlDTO dto = new NomenclatureUrlDTO("L_PAYS", "https://registry/codes-lists/L_PAYS");
        Mockito.when(nomenclatureService.getNomenclaturesUrls("my-q-id")).thenReturn(List.of(dto));

        // When we fetch the nomenclature URLs
        mockMvc.perform(get("/api/questionnaires/my-q-id/nomenclatures/urls")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the URL list is returned
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":\"L_PAYS\",\"url\":\"https://registry/codes-lists/L_PAYS\"}]"));
    }

    @Test
    @DisplayName("Should fetch questionnaires nomenclatures")
    void getAllNomenclatures_success() throws Exception {
        // Given nomenclatures
        List<NomenclatureDTO> nomenclatures = List.of(new NomenclatureDTO("id", "label", "version", "urn", null, "theme", "refYear"));
        Mockito.when(nomenclatureService.getAllNomenclatures()).thenReturn(nomenclatures);
        String expectedJSON = "[{\"id\":\"id\",\"label\":\"label\",\"version\":\"version\",\"urn\":\"urn\",\"theme\":\"theme\",\"referenceYear\":\"refYear\"}]";

        // When we fetch the questionnaire nomenclatures
        mockMvc.perform(get("/api/nomenclatures")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the nomenclatures are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

}
