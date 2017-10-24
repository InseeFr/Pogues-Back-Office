package fr.insee.pogues.search.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import fr.insee.pogues.search.model.DDIItem;
import fr.insee.pogues.search.model.DataCollectionContext;
import fr.insee.pogues.search.model.PoguesQuery;
import fr.insee.pogues.search.model.ResponseSearchItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;


@Service
public class SearchServiceImpl implements SearchService {
    
	@Autowired
    private PoguesItemRepository poguesItemRepository;

    public List<ResponseSearchItem> searchByLabel(PoguesQuery query, MultiValueMap<String, String> params) throws Exception {
        return poguesItemRepository.findByLabel(query, params);
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
