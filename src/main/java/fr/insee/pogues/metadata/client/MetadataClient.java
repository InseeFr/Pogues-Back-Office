package fr.insee.pogues.metadata.client;

import fr.insee.pogues.exception.IllegalFlowControlException;
import java.util.List;

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
	
	List<Serie> getSeries();
	
	List<Operation> getOperationsBySerieId(String id) throws IllegalFlowControlException.PoguesClientException;
	
	Serie getSerieById(String id);
	
	Operation getOperationById(String id) throws Exception;
	
}
