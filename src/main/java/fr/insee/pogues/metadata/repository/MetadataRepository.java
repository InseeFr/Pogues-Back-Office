package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;

import java.util.List;

public interface MetadataRepository {

	List<Unit> getUnits() throws Exception;
}
