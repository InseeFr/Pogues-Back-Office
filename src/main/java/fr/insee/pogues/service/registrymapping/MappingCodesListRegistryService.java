package fr.insee.pogues.service.registrymapping;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.exception.mapping.codes.list.registre.MappingNotFoundException;
import fr.insee.pogues.exception.mapping.codes.list.registre.PoguesCodesListAlreadyMappedException;
import fr.insee.pogues.exception.mapping.codes.list.registre.RegistreCodesListAlreadyMappedException;
import fr.insee.pogues.persistence.repository.MappingCodesListRegistreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MappingCodesListRegistryService implements MappingRegistryService {

    private final MappingCodesListRegistreRepository repository;

    public MappingCodesListRegistreDB create(
            String poguesCodesListId,
            UUID registreCodesListId) {

        if (repository.existsByPoguesCodesListId(poguesCodesListId)) {
            throw new PoguesCodesListAlreadyMappedException(poguesCodesListId);
        }

        if (repository.existsByRegistreCodesListId(registreCodesListId)) {
            throw new RegistreCodesListAlreadyMappedException(registreCodesListId.toString());
        }

        MappingCodesListRegistreDB entity = new MappingCodesListRegistreDB();
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        return repository.save(entity);
    }

    public List<MappingCodesListRegistreDB> getAll() {

        return repository.findAll();
    }

    public MappingCodesListRegistreDB update(
            Long id,
            UUID registreCodesListId) {

        MappingCodesListRegistreDB mapping =
                repository.findById(id)
                        .orElseThrow(() ->
                                new MappingNotFoundException(id)
                        );

        boolean registreIdAlreadyUsed =
                repository.existsByRegistreCodesListId(registreCodesListId);

        boolean registreIdChanged =
                !registreCodesListId.equals(
                        mapping.getRegistreCodesListId()
                );

        if (registreIdAlreadyUsed && registreIdChanged) {
            throw new RegistreCodesListAlreadyMappedException(
                    registreCodesListId.toString()
            );
        }

        mapping.setRegistreCodesListId(registreCodesListId);

        return repository.save(mapping);
    }

    public void delete(Long id) {

        if (!repository.existsById(id)) {
            throw new MappingNotFoundException(id);
        }

        repository.deleteById(id);
    }
}