package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.model.pogues.DataCollectionContext;

import java.util.List;

public interface MetadataService {

    List<Unit> getUnits() throws Exception;
    List<DataCollection> getSeries() throws Exception;
    List<DataCollection> getOperationsByIdSerie(String idSerie) throws Exception;
    List<DataCollection> getColletionsByIdOperation(String idOperation) throws Exception;
    DataCollectionContext getCollectionContextFromIdCollection(String idCollection) throws Exception;
}
