package fr.insee.pogues.controller;

import fr.insee.pogues.model.dto.nomenclatures.ExtendedNomenclatureDTO;
import fr.insee.pogues.model.dto.nomenclatures.ExternalLinkDTO;
import fr.insee.pogues.model.dto.nomenclatures.NomenclatureDTO;
import fr.insee.pogues.service.NomenclatureService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NomenclatureController.class)
class NomenclatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NomenclatureService nomenclatureService;

    @Test
    @DisplayName("Should fetch questionnaires nomenclatures")
    void getQuestionnaireVariables_success() throws Exception {
        // Given a questionnaire with nomenclatures
        NomenclatureDTO nomenclature = new NomenclatureDTO("id", "label", "version", new ExternalLinkDTO("urn"));
        ExtendedNomenclatureDTO extendedNomenclature = new ExtendedNomenclatureDTO(nomenclature, List.of("Q1", "Q2"));
        Mockito.when(nomenclatureService.getQuestionnaireNomenclatures("my-q-id")).thenReturn(List.of(extendedNomenclature));
        String expectedJSON = "[{\"id\":\"id\",\"label\":\"label\",\"version\":\"version\",\"externalLink\":{\"urn\":\"urn\"},\"relatedQuestionNames\":[\"Q1\",\"Q2\"]}]";

        // When we fetch the questionnaire nomenclatures
        mockMvc.perform(get("/api/questionnaires/my-q-id/nomenclatures")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                // Then we receive a 200 and the nomenclatures are returned
                .andExpect(status().isOk())
                .andExpect(content().string(expectedJSON));
    }

}
