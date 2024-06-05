package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;

import java.util.List;

public interface MagmaClient {

    List<Serie> getSeries() throws Exception;
    Serie getSerieById(String id) throws Exception;
    List<Operation> getOperationsByIdSerie(String idSerie) throws Exception;
    Operation getOperationById(String idOperation) throws Exception;
}
