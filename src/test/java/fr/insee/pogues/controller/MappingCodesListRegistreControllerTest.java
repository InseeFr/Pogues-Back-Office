package fr.insee.pogues.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.configuration.log.LogInterceptor;
import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;
import fr.insee.pogues.mapper.MappingCodesListRegistreMapper;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreCreateDTO;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreResponseDTO;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreUpdateDTO;
import fr.insee.pogues.service.registrymapping.MappingCodesListRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableMethodSecurity
@WebMvcTest(MappingCodesListRegistreController.class)
class MappingCodesListRegistreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MappingCodesListRegistryService service;

    @Autowired
    private MappingCodesListRegistreMapper mapper;

    @MockitoBean
    private LogInterceptor logInterceptor;

    @TestConfiguration
    static class TestConfig {

        @Bean
        public MappingCodesListRegistryService service() {

            return Mockito.mock(
                    MappingCodesListRegistryService.class
            );
        }

        @Bean
        public MappingCodesListRegistreMapper mapper() {

            return Mockito.mock(
                    MappingCodesListRegistreMapper.class
            );
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @BeforeEach
    void setup() {

        Mockito.reset(service, mapper, logInterceptor);
        Mockito.when(logInterceptor.preHandle(
                Mockito.any(),
                Mockito.any(),
                Mockito.any()
        )).thenReturn(true);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate() throws Exception {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreCreateDTO createDTO =
                new MappingCodesListRegistreCreateDTO(
                        poguesCodesListId,
                        registreCodesListId
                );

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(1L);
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        MappingCodesListRegistreResponseDTO responseDTO =
                new MappingCodesListRegistreResponseDTO(
                        1L,
                        poguesCodesListId,
                        registreCodesListId
                );

        when(service.create(
                poguesCodesListId,
                registreCodesListId
        )).thenReturn(entity);

        when(mapper.toDto(entity))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/mapping-codeslist")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.poguesCodesListId")
                        .value("AGE"))
                .andExpect(jsonPath("$.registreCodesListId")
                        .value(registreCodesListId.toString()));

        verify(service)
                .create(poguesCodesListId, registreCodesListId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate_Conflict() throws Exception {

        UUID registreCodesListId = UUID.randomUUID();

        when(service.create(any(), any()))
                .thenThrow(new PoguesCodesListAlreadyMappedException("AGE"));

        mockMvc.perform(post("/api/mapping-codeslist")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "poguesCodesListId":"AGE",
                          "registreCodesListId":"%s"
                        }
                    """.formatted(registreCodesListId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Pogues mapping already exists"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Mapping already exists for poguesCodesListId: AGE"));

        verify(service).create(any(), any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreate_BadRequest() throws Exception {

        mockMvc.perform(post("/api/mapping-codeslist")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "poguesCodesListId":"",
                          "registreCodesListId":null
                        }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @WithMockUser(username = "user")
    void testCreate_Forbidden_ForUser() throws Exception {

        UUID registreCodesListId = UUID.randomUUID();

        mockMvc.perform(post("/api/mapping-codeslist")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "poguesCodesListId":"AGE",
                      "registreCodesListId":"%s"
                    }
                    """.formatted(registreCodesListId)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAll() throws Exception {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(1L);
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        MappingCodesListRegistreResponseDTO responseDTO =
                new MappingCodesListRegistreResponseDTO(
                        1L,
                        poguesCodesListId,
                        registreCodesListId
                );

        when(service.getAll())
                .thenReturn(List.of(entity));

        when(mapper.toDto(entity))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/mapping-codeslist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].poguesCodesListId")
                        .value("AGE"));

        verify(service).getAll();
        verify(mapper).toDto(entity);
    }

    @Test
    @WithMockUser(username = "user", roles = {"WEBCLIENT"})
    void testGetAll_Authorized_ForUser() throws Exception {

        when(service.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/mapping-codeslist"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void testGetAll_Unauthorized_WhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/api/mapping-codeslist"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdate() throws Exception {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreUpdateDTO updateDTO =
                new MappingCodesListRegistreUpdateDTO(
                        registreCodesListId
                );

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(1L);
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        MappingCodesListRegistreResponseDTO responseDTO =
                new MappingCodesListRegistreResponseDTO(
                        1L,
                        poguesCodesListId,
                        registreCodesListId
                );

        when(service.update(eq(1L), any(UUID.class)))
                .thenReturn(entity);

        when(mapper.toDto(entity))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/mapping-codeslist/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.registreCodesListId")
                        .value(registreCodesListId.toString()));

        verify(service)
                .update(1L, registreCodesListId);
        verify(service).update(eq(1L), any(UUID.class));
        verify(mapper).toDto(entity);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdate_NotFound() throws Exception {

        UUID registreCodesListId = UUID.randomUUID();

        when(service.update(eq(1L), any(UUID.class)))
                .thenThrow(new MappingNotFoundException(1L));

        mockMvc.perform(put("/api/mapping-codeslist/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {"registreCodesListId":"%s"}
                    """.formatted(registreCodesListId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Mapping not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Mapping not found with id: 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdate_Conflict_Registre() throws Exception {

        UUID registreCodesListId = UUID.randomUUID();

        when(service.update(eq(1L), any(UUID.class)))
                .thenThrow(
                        new RegistreCodesListAlreadyMappedException(
                                registreCodesListId.toString()
                        )
                );

        mockMvc.perform(
                        put("/api/mapping-codeslist/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "registreCodesListId":"%s"
                            }
                            """.formatted(registreCodesListId))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title")
                        .value("Registre mapping already exists"))
                .andExpect(jsonPath("$.status")
                        .value(409))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Mapping already exists for registreCodesListId: "
                                        + registreCodesListId
                        ));
    }

    @Test
    @WithMockUser(username = "user")
    void testUpdate_Forbidden_ForUser() throws Exception {

        UUID registreCodesListId = UUID.randomUUID();

        mockMvc.perform(put("/api/mapping-codeslist/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "registreCodesListId":"%s"
                    }
                    """.formatted(registreCodesListId)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDelete() throws Exception {

        mockMvc.perform(delete("/api/mapping-codeslist/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service)
                .delete(1L);
    }

    @Test
    @WithMockUser(username = "user")
    void testDelete_Forbidden_ForUser() throws Exception {

        mockMvc.perform(delete("/api/mapping-codeslist/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(service, never()).delete(anyLong());
    }
}