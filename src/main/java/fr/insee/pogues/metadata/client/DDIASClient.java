package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.Unit;

import java.util.List;

public interface DDIASClient {
    List<Unit> getUnits() throws Exception;
}
