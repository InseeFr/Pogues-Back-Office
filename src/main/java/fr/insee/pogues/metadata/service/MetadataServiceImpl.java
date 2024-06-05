package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.magma.Frequence;
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
    public List<DataCollection> getSeries() throws Exception {
        List<Serie> series = metadataRepository.getSeries();
        return createDataCollectionsFromSeries(series);
    }

    @Override
    public List<DataCollection> getOperationsByIdSerie(String idSerie) throws Exception {
        List<Operation> operations = metadataRepository.getOperationsByIdSerie(idSerie);
        return createDataCollectionsFromOperations(operations);
    }

    @Override
    public List<DataCollection> getColletionsByIdOperation(String idOperation) throws Exception {
        Operation operation = metadataRepository.getOperationById(idOperation);
        Serie serie = metadataRepository.getSerieById(operation.getSerie().getId());
        Frequence frequence = serie.getFrequence();
        return createCollectionsFromOperation(operation, frequence);
    }

    @Override
    public DataCollectionContext getCollectionContextFromIdCollection(String idCollection) throws Exception {
        String idOperation = idCollectionToIdOperation(idCollection);
        Operation operation = metadataRepository.getOperationById(idOperation);
        return createDataCollectionContext(idCollection, operation);
    }
}
