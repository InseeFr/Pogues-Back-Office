package fr.insee.pogues.search.repository;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;

import java.util.List;

public interface PoguesItemRepository {

    IndexResponse save(String type, PoguesItem item) throws Exception;
    List<PoguesHit> findByLabel(String label, String ...types) throws Exception;
    DeleteResponse delete(String type, String id) throws Exception;
}
