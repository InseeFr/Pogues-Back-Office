package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.DDIItem;
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

    public List<DDIItem> searchByLabel(String label, String... types) throws Exception {
        return poguesItemRepository.findByLabel(label, types);
    }

    public DeleteResponse delete(String type, String id) throws Exception {
        return poguesItemRepository.delete(type, id);
    }

    @Override
    public List<DDIItem> getSubGroups() throws Exception {
        return poguesItemRepository.getSubGroups();
    }

    @Override
    public List<DDIItem> getStudyUnits(String subGroupId) throws Exception {
        return poguesItemRepository.getStudyUnits(subGroupId);
    }

    @Override
    public List<DDIItem> getDataCollections(String operationId) throws Exception {
        return poguesItemRepository.getDataCollections(operationId);
    }
}
