package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {


    private static final Logger logger = LogManager.getLogger(SearchServiceImpl.class);

    @Autowired
    private PoguesItemRepository poguesItemRepository;

    @Override
    public void save(String type, PoguesItem item) throws Exception {
        try {
            poguesItemRepository.save(type, item);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            throw e;
        }
    }

    public SearchResponse searchByLabel(String label, String... types) throws Exception {
        try {
             return poguesItemRepository.findByLabel(label, types);
        } catch (Exception e) {
            logger.error(e.getStackTrace());
            throw e;
        }
    }

}
