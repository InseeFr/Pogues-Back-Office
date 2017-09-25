package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;

import java.util.List;


public interface SearchService {

    IndexResponse save(String type, PoguesItem item) throws Exception;
    List<DDIItem> searchByLabel(String label, String... types) throws Exception;
    DeleteResponse delete(String type, String id) throws Exception;
    List<DDIItem> getSubGroups() throws Exception;
    List<DDIItem> getStudyUnits(String seriesId) throws Exception;
    List<DDIItem> getDataCollections(String operationId) throws Exception;
}
