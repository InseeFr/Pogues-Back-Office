package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;

import java.util.List;

public interface MetadataRepository {

    ColecticaItem findById(String id) throws Exception;
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
    List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;
	List<Unit> getUnits() throws Exception;
	String getDDIDocument(String id) throws Exception;
}
