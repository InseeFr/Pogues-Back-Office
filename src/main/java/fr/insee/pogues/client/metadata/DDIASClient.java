package fr.insee.pogues.client.metadata;

import fr.insee.pogues.client.metadata.model.ddias.Unit;

import java.util.List;

public interface DDIASClient {
    List<Unit> getUnits() throws Exception;
}
