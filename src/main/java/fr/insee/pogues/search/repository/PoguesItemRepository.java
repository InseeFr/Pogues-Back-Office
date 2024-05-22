package fr.insee.pogues.search.repository;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;
import org.springframework.util.MultiValueMap;

import java.util.List;

public interface PoguesItemRepository {

	List<ResponseSearchItem> findByLabel(PoguesQuery query, MultiValueMap<String, String> params) throws Exception;

	List<DDIItem> getSubGroups() throws Exception;

	List<DDIItem> getStudyUnits(String seriesId) throws Exception;

	List<DDIItem> getDataCollections(String operationId) throws Exception;

	DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception;
}
