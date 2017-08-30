package fr.insee.pogues.search.repository;

import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;

public interface PoguesItemRepository {

    IndexResponse save(String type, PoguesItem item) throws Exception;
    SearchResponse findByLabel(String label, String ...types) throws Exception;

}
