package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.exception.IllegalFlowControlException;
import fr.insee.pogues.metadata.model.*;

import java.util.List;

public interface MetadataRepository {

	ColecticaItem findById(String id) throws Exception;

	ColecticaItemRefList getChildrenRef(String id) throws Exception;

	List<ColecticaItem> getItems(ColecticaItemRefList refs) throws Exception;

	List<Unit> getUnits() throws Exception;

	String getDDIDocument(String id) throws Exception;

	String getCodeList(String id) throws Exception;

	List<Serie> getSeries() throws Exception;

	List<Operation> getOperationsBySerieId(String id) throws IllegalFlowControlException.PoguesClientException;

	Serie getSerieById(String id) throws Exception;

	Operation getOperationById(String group) throws Exception;
}
