package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(
        value = "feature.mock.metadata",
        havingValue = "true"
)
public class MetadataMockRepository implements MetadataRepository {

    @Override
    public List<Unit> getUnits() throws Exception {
        return List.of();
    }

    @Override
    public List<Serie> getSeries() throws Exception {
        return List.of();
    }

    @Override
    public Serie getSerieById(String id) throws Exception {
        return null;
    }

    @Override
    public List<Operation> getOperationsByIdSerie(String idSerie) throws Exception {
        return List.of();
    }

    @Override
    public Operation getOperationById(String idOperation) throws Exception {
        return null;
    }
}
