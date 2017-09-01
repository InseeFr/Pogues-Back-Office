package fr.insee.pogues.search.service;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger logger = LogManager.getLogger(SearchServiceImpl.class);

    @Autowired
    private PoguesItemRepository poguesItemRepository;

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        try {
            return poguesItemRepository.save(type, item);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public List<PoguesHit> searchByLabel(String label, String... types) throws Exception {
        try {
            return poguesItemRepository.findByLabel(label, types);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

    public DeleteResponse delete(String type, String id) throws Exception {
        try {
            return poguesItemRepository.delete(type, id);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            throw e;
        }
    }

}
