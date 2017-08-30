package fr.insee.pogues.search.repository;

import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class PoguesItemRepositoryImpl implements PoguesItemRepository {

    @Value("${fr.insee.pogues.elasticsearch.index.name}")
    String index;

    @Autowired
    Client client;

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        return client.prepareIndex(index, type, item.getId())
                .setSource(item)
                .get();
    }

    @Override
    public SearchResponse findByLabel(String label, String... types) throws Exception {
        return client.prepareSearch(index)
                .setTypes(types)
                .setQuery(QueryBuilders.termQuery("label",label))
                .get();
    }
}
