package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;


public interface SearchService {

    IndexResponse save(String type, PoguesItem item) throws Exception;
    SearchResponse searchByLabel(String label, String... types) throws Exception;
}
