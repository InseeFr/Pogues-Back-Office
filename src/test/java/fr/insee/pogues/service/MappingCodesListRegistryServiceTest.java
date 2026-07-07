package fr.insee.pogues.service;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;
import fr.insee.pogues.persistence.repository.MappingCodesListRegistreRepository;
import fr.insee.pogues.service.registrymapping.MappingCodesListRegistryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MappingCodesListRegistryServiceTest {

    private MappingCodesListRegistreRepository repository;

    private MappingCodesListRegistryService service;

    @BeforeEach
    void setUp() {

        repository = mock(MappingCodesListRegistreRepository.class);

        service = new MappingCodesListRegistryService(repository);
    }

    @Test
    void testCreate_Success() {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(1L);
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        when(repository.existsByPoguesCodesListId(
                poguesCodesListId)).thenReturn(false);

        when(repository.save(any(
                MappingCodesListRegistreDB.class)))
                .thenReturn(entity);

        MappingCodesListRegistreDB result =
                service.create(
                        poguesCodesListId,
                        registreCodesListId
                );

        assertNotNull(result);

        assertEquals(
                poguesCodesListId,
                result.getPoguesCodesListId()
        );

        assertEquals(
                registreCodesListId,
                result.getRegistreCodesListId()
        );

        verify(repository, times(1))
                .save(any(MappingCodesListRegistreDB.class));
    }

    @Test
    void testCreate_Conflict_Pogues() {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        when(repository.existsByPoguesCodesListId(
                poguesCodesListId)).thenReturn(true);

        PoguesCodesListAlreadyMappedException exception =
                assertThrows(
                        PoguesCodesListAlreadyMappedException.class,
                        () -> service.create(poguesCodesListId, registreCodesListId)
                );

        assertEquals(
                "Mapping already exists for poguesCodesListId: AGE",
                exception.getMessage()
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testCreate_Conflict_Registre() {

        String poguesCodesListId = "AGE";

        UUID registreCodesListId = UUID.randomUUID();

        when(repository.existsByPoguesCodesListId(poguesCodesListId))
                .thenReturn(false);

        when(repository.existsByRegistreCodesListId(registreCodesListId))
                .thenReturn(true);

        RegistreCodesListAlreadyMappedException exception =
                assertThrows(
                        RegistreCodesListAlreadyMappedException.class,
                        () -> service.create(poguesCodesListId, registreCodesListId)
                );

        assertEquals(
                "Mapping already exists for registreCodesListId: " + registreCodesListId,
                exception.getMessage()
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testGetAll() {

        MappingCodesListRegistreDB entity1 =
                new MappingCodesListRegistreDB();

        entity1.setId(1L);

        MappingCodesListRegistreDB entity2 =
                new MappingCodesListRegistreDB();

        entity2.setId(2L);

        when(repository.findAll())
                .thenReturn(List.of(entity1, entity2));

        List<MappingCodesListRegistreDB> result =
                service.getAll();

        assertEquals(2, result.size());

        verify(repository, times(1))
                .findAll();
    }

    @Test
    void testUpdate_Success() {

        Long id = 1L;

        UUID newRegistreCodesListId =
                UUID.randomUUID();

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(id);
        entity.setPoguesCodesListId("AGE");

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        when(repository.existsByRegistreCodesListId(newRegistreCodesListId))
                .thenReturn(false);

        when(repository.save(any(
                MappingCodesListRegistreDB.class)))
                .thenReturn(entity);

        MappingCodesListRegistreDB result =
                service.update(
                        id,
                        newRegistreCodesListId
                );

        assertEquals(
                newRegistreCodesListId,
                result.getRegistreCodesListId()
        );

        verify(repository, times(1))
                .save(entity);
    }

    @Test
    void testUpdate_NotFound() {

        Long id = 1L;

        UUID newRegistreCodesListId = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        MappingNotFoundException exception =
                assertThrows(
                        MappingNotFoundException.class,
                        () -> service.update(id, newRegistreCodesListId)
                );

        assertEquals(
                "Mapping not found with id: 1",
                exception.getMessage()
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testUpdate_Conflict_Registre() {

        Long id = 1L;

        UUID newRegistreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreDB entity = new MappingCodesListRegistreDB();
        entity.setId(id);
        entity.setRegistreCodesListId(UUID.randomUUID());

        when(repository.findById(id))
                .thenReturn(Optional.of(entity));

        when(repository.existsByRegistreCodesListId(newRegistreCodesListId))
                .thenReturn(true);

        RegistreCodesListAlreadyMappedException exception =
                assertThrows(
                        RegistreCodesListAlreadyMappedException.class,
                        () -> service.update(id, newRegistreCodesListId)
                );

        assertEquals(
                "Mapping already exists for registreCodesListId: " + newRegistreCodesListId,
                exception.getMessage()
        );

        verify(repository, never()).save(any());
    }

    @Test
    void testDelete_Success() {

        Long id = 1L;

        when(repository.existsById(id))
                .thenReturn(true);

        service.delete(id);

        verify(repository, times(1))
                .deleteById(id);
    }

    @Test
    void testDelete_NotFound() {

        Long id = 1L;

        when(repository.existsById(id))
                .thenReturn(false);

        MappingNotFoundException exception =
                assertThrows(
                        MappingNotFoundException.class,
                        () -> service.delete(id)
                );

        assertEquals(
                "Mapping not found with id: 1",
                exception.getMessage()
        );

        verify(repository, never()).deleteById(any());
    }
}