package fr.insee.pogues.metadata.service;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;
import fr.insee.pogues.metadata.model.pogues.Collection;

import java.util.List;

public interface MetadataService {
    List<Unit> getUnits() throws Exception;
    List<Serie> getSeries() throws Exception;
    List<Operation> getOperationsByIdSerie(String idSerie) throws Exception;
    List<Collection> getColletionsByIdOperation(String idOperation) throws Exception;
    List<>
}
