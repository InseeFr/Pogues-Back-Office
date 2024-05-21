package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import fr.insee.pogues.metadata.model.pogues.Collection;
import fr.insee.pogues.metadata.model.pogues.CollectionContext;

import java.util.List;

public interface MetadataService {

    List<Unit> getUnits() throws Exception;
    List<Serie> getSeries() throws Exception;
    List<Operation> getOperationsByIdSerie(String idSerie) throws Exception;
    List<Collection> getColletionsByIdOperation(String idOperation) throws Exception;
    CollectionContext getCollectionContextFromIdCollection(String idCollection) throws Exception;
}
