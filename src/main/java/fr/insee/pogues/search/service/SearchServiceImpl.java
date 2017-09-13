package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private PoguesItemRepository poguesItemRepository;

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        return poguesItemRepository.save(type, item);
    }

    public List<PoguesHit> searchByLabel(String label, String... types) throws Exception {
        return poguesItemRepository.findByLabel(label, types);
    }

    public DeleteResponse delete(String type, String id) throws Exception {
        return poguesItemRepository.delete(type, id);
    }

    @Override
    public List<PoguesHit> getSeries() throws Exception {
        return poguesItemRepository.getSeries();
    }

    @Override
    public List<PoguesHit> getOperations(String seriesId) throws Exception {
        return poguesItemRepository.getOperations(seriesId);
    }
}
