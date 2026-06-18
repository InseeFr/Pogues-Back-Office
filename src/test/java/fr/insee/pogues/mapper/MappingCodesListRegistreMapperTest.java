package fr.insee.pogues.mapper;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MappingCodesListRegistreMapperTest {

    private MappingCodesListRegistreMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MappingCodesListRegistreMapper();
    }

    @Test
    void testToDto() {

        UUID registreCodesListId = UUID.randomUUID();

        MappingCodesListRegistreDB entity =
                new MappingCodesListRegistreDB();

        entity.setId(1L);
        entity.setPoguesCodesListId("AGE");
        entity.setRegistreCodesListId(registreCodesListId);

        MappingCodesListRegistreResponseDTO dto =
                mapper.toDto(entity);

        assertNotNull(dto);

        assertEquals(1L, dto.id());
        assertEquals("AGE", dto.poguesCodesListId());
        assertEquals(
                registreCodesListId,
                dto.registreCodesListId()
        );
    }

    @Test
    void testToDto_WithNullEntity() {

        MappingCodesListRegistreResponseDTO dto =
                mapper.toDto(null);

        assertNull(dto);
    }
}