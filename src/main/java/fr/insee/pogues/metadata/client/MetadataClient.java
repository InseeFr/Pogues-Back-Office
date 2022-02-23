package fr.insee.pogues.metadata.client;

import java.util.List;

import fr.insee.pogues.exceptions.PoguesClientException;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Operation;
import fr.insee.pogues.metadata.model.Serie;
import fr.insee.pogues.metadata.model.Unit;

public interface MetadataClient {

    ColecticaItem getItem(String id) throws Exception;
    
    List<ColecticaItem> getItems(ColecticaItemRefList query) throws Exception;
    
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
    
	List<Unit> getUnits() throws Exception;
	
    String getDDIDocument(String id) throws Exception;
    
	String getCodeList(String id) throws Exception;
	
	List<Serie> getSeries() throws Exception;
	
	List<Operation> getOperationsBySerieId(String id) throws PoguesClientException;
	
	Serie getSerieById(String id) throws Exception;
	
	Operation getOperationById(String id) throws Exception;
	
}
