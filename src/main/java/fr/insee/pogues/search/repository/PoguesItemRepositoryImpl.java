package fr.insee.pogues.search.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PoguesItemRepositoryImpl implements PoguesItemRepository {

    @Value("${fr.insee.pogues.elasticsearch.index.name}")
    String index;

    @Autowired
    Client client;

    @Override
    public IndexResponse save(String type, PoguesItem item) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = mapper.writeValueAsBytes(item);
        return client.prepareIndex(index, type, item.getId())
                .setSource(data)
                .get();
    }

    @Override
    public List<PoguesHit> findByLabel(String label, String... types) throws Exception {
        SearchResponse response = client.prepareSearch(index)
                .setTypes(types)
                .setQuery(QueryBuilders.matchQuery("label", label))
                .get();
        return mapResponse(response);
    }

    @Override
    public List<PoguesHit> getSeries() throws Exception {
        SearchResponse response = client.prepareSearch(index)
                .setTypes("series")
                .get();
        return mapResponse(response);
    }

    @Override
    public List<PoguesHit> getOperations(String seriesId) throws Exception {
        SearchResponse response = client.prepareSearch(index)
                .setTypes("operation")
                .setQuery(QueryBuilders.termQuery("parent.keyword", seriesId))
                .get();
        return mapResponse(response);
    }

    @Override
    public DeleteResponse delete(String type, String id) throws Exception {
        return client.prepareDelete(index, type, id).get();
    }

    private List<PoguesHit> mapResponse(SearchResponse response) {
        List<SearchHit> esHits = Arrays.asList(response.getHits().getHits());
        return esHits
                .stream()
                .map(hit -> new PoguesHit(
                        hit.getId(),
                        hit.getSource().get("label").toString(),
                        hit.getSource().get("parent").toString(),
                        hit.getType()
                ))
                .collect(Collectors.toList());
    }
}
