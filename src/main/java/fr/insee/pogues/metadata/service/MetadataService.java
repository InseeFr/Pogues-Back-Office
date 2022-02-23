package fr.insee.pogues.metadata.service;

import java.util.List;

import fr.insee.pogues.exceptions.PoguesClientException;
import fr.insee.pogues.exceptions.PoguesException;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.model.ColecticaItemRefList;
import fr.insee.pogues.metadata.model.Context;
import fr.insee.pogues.metadata.model.DataCollectionOut;
import fr.insee.pogues.metadata.model.OperationOut;
import fr.insee.pogues.metadata.model.SerieOut;
import fr.insee.pogues.metadata.model.Unit;

public interface MetadataService {

    ColecticaItem getItem(String id) throws Exception;
    
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
    
    List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;
    
    List<Unit> getUnits() throws Exception;
    
    String getDDIDocument(String id) throws Exception;
    
	String getCodeList(String id) throws Exception;
	
	List<SerieOut> getSeries() throws Exception;
	
	List<OperationOut> getOperationsBySerieId(String id) throws PoguesClientException, PoguesException;
	
	List<DataCollectionOut> getDataCollectionsByOperationId(String id) throws Exception;

	Context getContextFromDataCollection(String id) throws Exception;
}
