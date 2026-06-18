package fr.insee.pogues.mapper;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.model.dto.mapping.codes.list.registre.MappingCodesListRegistreResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MappingCodesListRegistreMapper {

    public MappingCodesListRegistreResponseDTO toDto(MappingCodesListRegistreDB entity) {
        if (entity == null) {
            return null;
        }

        return new MappingCodesListRegistreResponseDTO(
                entity.getId(),
                entity.getPoguesCodesListId(),
                entity.getRegistreCodesListId()
        );
    }
}