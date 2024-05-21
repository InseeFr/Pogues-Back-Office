package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.magma.Operation;
import fr.insee.pogues.metadata.model.magma.Serie;

import java.util.List;

public class MagmaClientImpl implements MagmaClient {

    @Override
    public List<Serie> getSeries() throws Exception {
        return List.of();
    }

    @Override
    public List<Operation> getOperationsByIdSerie(String id) throws Exception {
        return List.of();
    }
}
