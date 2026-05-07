package fr.insee.pogues.persistence.repository;

import fr.insee.pogues.domain.entity.db.MappingCodesListRegistreDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MappingCodesListRegistreRepository
        extends JpaRepository<MappingCodesListRegistreDB, Long> {

    Optional<MappingCodesListRegistreDB> findByPoguesCodesListId(String poguesCodesListId);
}