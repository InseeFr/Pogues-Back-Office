package fr.insee.pogues.metadata.service;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.metadata.model.*;
import fr.insee.pogues.model.CodeList;

import java.util.List;

public interface MetadataService {

    ColecticaItem getItem(String id) throws Exception;
    ColecticaItemRefList getChildrenRef(String id) throws Exception;
    List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;
    List<Unit> getUnits() throws Exception;
    String getDDIDocument(String id) throws Exception;
	String getCodeList(String id) throws Exception;

	List<SerieOut> getSeries() throws Exception;
	
	List<OperationOut> getOperationsBySerieId(String id) throws IllegalFlowControlException.PoguesClientException, PoguesException;
	
	List<DataCollectionOut> getDataCollectionsByOperationId(String id) throws Exception;

	Context getContextFromDataCollection(String id) throws Exception;

}
