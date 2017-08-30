package fr.insee.pogues.search.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class PoguesItemRepositoryImpl implements PoguesItemRepository {

    String index;

    @Autowired
    Environment env;

    @Autowired
    Client client;

    @PostConstruct
    public void init(){
        index = env.getProperty("fr.insee.pogues.elasticsearch.index.name");
    }

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = mapper.writeValueAsBytes(item);
        return client.prepareIndex(index, type, item.getId())
                .setSource(data)
                .get();
    }

    @Override
    public SearchResponse findByLabel(String label, String... types) throws Exception {
        return client.prepareSearch(index)
                .setTypes(types)
                .setQuery(QueryBuilders.matchQuery("label", label))
                .get();
    }
}
