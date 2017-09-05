package fr.insee.pogues.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.model.PoguesItem;
import fr.insee.pogues.search.repository.PoguesItemRepository;
import fr.insee.pogues.search.repository.PoguesItemRepositoryImpl;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestSearchRepository {

    @Mock
    Client client;

    @InjectMocks
    PoguesItemRepository repository;

    @Before
    public void setUp() {
        repository = spy(new PoguesItemRepositoryImpl()); // <- class under test
        initMocks(this);
    }

    @Test
    public void saveTest() throws Exception {
        IndexResponse response = mock(IndexResponse.class);
        when(response.toString()).thenReturn("response");
        IndexRequestBuilder irb = mock(IndexRequestBuilder.class);
        PoguesItem item = new PoguesItem("foo", "0", "bar");
        ObjectMapper mapper = new ObjectMapper();
        byte[] data = mapper.writeValueAsBytes(item);
        when(irb.setSource(data)).thenReturn(irb);
        when(irb.get()).thenReturn(response);
        when(client.prepareIndex(null, "anything", "foo")).thenReturn(irb);
        Assert.assertEquals(response, repository.save("anything", item));
    }

    @Test
    public void deleteTest() throws Exception {
        DeleteResponse response = mock(DeleteResponse.class);
        when(response.toString()).thenReturn("response");
        DeleteRequestBuilder drb = mock(DeleteRequestBuilder.class);
        when(drb.get()).thenReturn(response);
        when(client.prepareDelete(null, "foo", "bar")).thenReturn(drb);
        Assert.assertEquals(response, repository.delete("foo", "bar"));
    }

    @Test
    public void findByLabelTest() throws Exception {
        SearchResponse response = mock(SearchResponse.class);
        SearchRequestBuilder srb = mock(SearchRequestBuilder.class);
        SearchHits shs = mock(SearchHits.class);
        SearchHit sh = mock(SearchHit.class);
        when(client.prepareSearch(null)).thenReturn(srb);
        when(srb.setTypes("anything")).thenReturn(srb);
        when(srb.setQuery(QueryBuilders.matchQuery("label", "bar"))).thenReturn(srb);
        when(srb.get()).thenReturn(response);
        when(response.getHits()).thenReturn(shs);
        when(sh.getId()).thenReturn("foo");
        when(sh.getSource()).thenReturn(new HashMap<String, Object>(){
            { put("label", "bar"); }
            { put("parent", "0"); }
        });
        when(sh.getType()).thenReturn("anything");
        when(shs.getHits()).thenReturn(new SearchHit[]{
           sh
        });
        List<PoguesHit> hits = repository.findByLabel("bar", "anything");
        Assert.assertEquals("anything", hits.get(0).getType());
        Assert.assertEquals("bar", hits.get(0).getLabel());
        Assert.assertEquals("foo", hits.get(0).getId());
    }
}
