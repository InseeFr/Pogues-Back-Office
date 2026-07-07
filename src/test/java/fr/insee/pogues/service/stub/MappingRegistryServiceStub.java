package fr.insee.pogues.service.stub;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;
import fr.insee.pogues.service.registrymapping.MappingRegistryService;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public class MappingRegistryServiceStub implements MappingRegistryService {

    @Setter
    List<MappingCodesListRegistreDB> allMappings = List.of();

    @Override
    public MappingCodesListRegistreDB create(String poguesCodesListId, UUID registreCodesListId) throws PoguesCodesListAlreadyMappedException, RegistreCodesListAlreadyMappedException {
        return null;
    }

    @Override
    public List<MappingCodesListRegistreDB> getAll() {
        return allMappings;
    }

    @Override
    public MappingCodesListRegistreDB update(Long id, UUID registreCodesListId) {
        return null;
    }

    @Override
    public void delete(Long id) {
        // do nothing on this mock
    }
}
