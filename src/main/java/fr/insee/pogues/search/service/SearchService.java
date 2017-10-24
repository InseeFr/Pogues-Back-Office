package fr.insee.pogues.search.service;

import java.util.List;

import org.springframework.util.MultiValueMap;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;


public interface SearchService {

    List<ResponseSearchItem> searchByLabel(PoguesQuery query, MultiValueMap<String, String> params) throws Exception;
    List<DDIItem> getSubGroups() throws Exception;
    List<DDIItem> getStudyUnits(String seriesId) throws Exception;
    List<DDIItem> getDataCollections(String operationId) throws Exception;
    DataCollectionContext getDataCollectionContext(String id) throws Exception;
    
}
