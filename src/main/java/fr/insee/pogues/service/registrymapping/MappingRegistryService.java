package fr.insee.pogues.service.registrymapping;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;

import java.util.List;
import java.util.UUID;

public interface MappingRegistryService {

    MappingCodesListRegistreDB create(String poguesCodesListId, UUID registreCodesListId) throws PoguesCodesListAlreadyMappedException, RegistreCodesListAlreadyMappedException;

    List<MappingCodesListRegistreDB> getAll();

    MappingCodesListRegistreDB update(Long id, UUID registreCodesListId);

    void delete(Long id) throws MappingNotFoundException;
}
