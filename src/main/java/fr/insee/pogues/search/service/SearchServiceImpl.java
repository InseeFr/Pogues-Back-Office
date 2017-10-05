package fr.insee.pogues.search.service;

import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;

@Service
public class SearchServiceImpl implements SearchService {

	@Value("${fr.insee.pogues.search.poguesItemRepository.impl}")
    private static final String poguesItemRepositoryImpl="PoguesItemFakeImpl";
    
	@Autowired
    @Qualifier(poguesItemRepositoryImpl)
    private PoguesItemRepository poguesItemRepository;

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        return poguesItemRepository.save(type, item);
    }

    public List<DDIItem> searchByLabel(String label, String... types) throws Exception {
        return poguesItemRepository.findByLabel(label, types);
    }

    public List<DDIItem> searchByLabelInSubgroup(String label, String subgroupId, String... types) throws Exception {
        return poguesItemRepository.findByLabelInSubGroup(label, subgroupId, types);
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
    
    @Override
    public DataCollectionContext getDataCollectionContext(String dataCollectionId) throws Exception{
    	 return poguesItemRepository.getDataCollectionContext(dataCollectionId);
    }
    
}
