package fr.insee.pogues.metadata.client;

import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Unit;

import java.util.List;

public interface MetadataClient {

    ColecticaItem getItem(String id) throws Exception;
    List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception;
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
	List<Unit> getUnits() throws Exception;
    String getDDIDocument(String id) throws Exception;
	String getCodeList(String id) throws Exception;
	
}
