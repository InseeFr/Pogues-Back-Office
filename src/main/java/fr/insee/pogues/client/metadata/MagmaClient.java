package fr.insee.pogues.client.metadata;

import fr.insee.pogues.client.metadata.model.magma.Operation;
import fr.insee.pogues.client.metadata.model.magma.Serie;

import java.util.List;

public interface MagmaClient {

    List<Serie> getSeries() throws Exception;
    Serie getSerieById(String id) throws Exception;
    List<Operation> getOperationsByIdSerie(String idSerie) throws Exception;
    Operation getOperationById(String idOperation) throws Exception;
}
