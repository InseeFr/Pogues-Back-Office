package fr.insee.pogues.service.metadata;

import fr.insee.pogues.client.metadata.DDIASClient;
import fr.insee.pogues.client.metadata.MagmaClient;
import fr.insee.pogues.client.metadata.model.ddias.Unit;
import fr.insee.pogues.client.metadata.model.magma.Frequence;
import fr.insee.pogues.client.metadata.model.magma.Operation;
import fr.insee.pogues.client.metadata.model.magma.Serie;
import fr.insee.pogues.client.metadata.model.pogues.DataCollection;
import fr.insee.pogues.client.metadata.model.pogues.DataCollectionContext;
import fr.insee.pogues.configuration.cache.CacheName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.insee.pogues.client.metadata.mapper.MetadataMapper.*;

@Service
@Slf4j
@AllArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    /**
     * DDIAS (DDI Access Service) Client is used of units & codeLists (suggester)
     */
    DDIASClient ddiasClient;

    /**
     * Magma Client is used of operations & series
     */
    MagmaClient magmaClient;


    @Override
    @Cacheable(CacheName.UNITS)
    public List<Unit> getUnits() throws Exception {
        return ddiasClient.getUnits();
    }

    @Override
    @Cacheable(CacheName.SERIES)
    public List<DataCollection> getSeries() throws Exception {
        List<Serie> series = magmaClient.getSeries();
        return createDataCollectionsFromSeries(series);
    }

    @Override
    @Cacheable(CacheName.SERIE)
    public List<DataCollection> getOperationsByIdSerie(String idSerie) throws Exception {
        List<Operation> operations = magmaClient.getOperationsByIdSerie(idSerie);
        return createDataCollectionsFromOperations(operations);
    }

    @Override
    public List<DataCollection> getColletionsByIdOperation(String idOperation) throws Exception {
        Operation operation = magmaClient.getOperationById(idOperation);
        Serie serie = magmaClient.getSerieById(operation.getSerie().getId());
        Frequence frequence = serie.getFrequence();
        return createCollectionsFromOperation(operation, frequence);
    }

    @Override
    public DataCollectionContext getCollectionContextFromIdCollection(String idCollection) throws Exception {
        String idOperation = idCollectionToIdOperation(idCollection);
        Operation operation = magmaClient.getOperationById(idOperation);
        return createDataCollectionContext(idCollection, operation);
    }
}
