package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.model.pogues.DataCollectionContext;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.pogues.metadata.service.MetadataConverter.*;

@Service
@Slf4j
public class MetadataServiceImpl implements MetadataService {

    @Autowired
    MetadataRepository metadataRepository;

    @Override
    public List<Unit> getUnits() throws Exception {
        return metadataRepository.getUnits();
    }

    @Override
    public List<Serie> getSeries() throws Exception {
        return metadataRepository.getSeries();
    }

    @Override
    public List<Operation> getOperationsByIdSerie(String idSerie) throws Exception {
        return metadataRepository.getOperationsByIdSerie(idSerie);
    }

    @Override
    public List<DataCollection> getColletionsByIdOperation(String idOperation) throws Exception {
        Operation operation = metadataRepository.getOperationById(idOperation);
        return createCollectionsFromOperation(operation);
    }

    @Override
    public DataCollectionContext getCollectionContextFromIdCollection(String idCollection) throws Exception {
        String idOperation = idCollectionToIdOperation(idCollection);
        Operation operation = metadataRepository.getOperationById(idOperation);
        return createDataCollectionContext(idCollection, operation);
    }
}
