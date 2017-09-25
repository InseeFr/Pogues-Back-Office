package fr.insee.pogues.metadata.service;

import java.util.List;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;
import fr.insee.pogues.search.model.PoguesItem;

public interface MetadataService {

    ColecticaItem getItem(String id) throws Exception;
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
    List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;
    List<String> getGroupIds() throws Exception;
    String getDDIDocument(String itemId, String groupId) throws Exception;
    PoguesItem getDDIRoot(String id) throws Exception;
	List<Unit> getUnits()throws Exception;
}
