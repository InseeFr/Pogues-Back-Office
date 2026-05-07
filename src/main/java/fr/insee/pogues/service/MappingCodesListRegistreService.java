package fr.insee.pogues.service;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import fr.insee.pogues.persistence.repository.MappingCodesListRegistreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MappingCodesListRegistreService {

    private final MappingCodesListRegistreRepository repository;

    public MappingCodesListRegistreDB create(String poguesCodesListId, UUID registreCodesListId) {

        repository.findByPoguesCodesListId(poguesCodesListId)
                .ifPresent(m -> {
                    throw new RuntimeException("Mapping already exists");
                });

        MappingCodesListRegistreDB entity = new MappingCodesListRegistreDB();
        entity.setPoguesCodesListId(poguesCodesListId);
        entity.setRegistreCodesListId(registreCodesListId);

        return repository.save(entity);
    }

    public List<MappingCodesListRegistreDB> getAll() {
        return repository.findAll();
    }
}
