package fr.insee.pogues.search;

import fr.insee.pogues.search.repository.PoguesItemRepository;
import fr.insee.pogues.search.repository.PoguesItemRepositoryImpl;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestSearchRepository {

    @Mock
    RestHighLevelClient client;

    @InjectMocks
    PoguesItemRepository repository;

    @Before
    public void setUp() {
        repository = spy(new PoguesItemRepositoryImpl()); // <- class under test
        initMocks(this);
    }

    @Test
    public void saveTest() throws Exception {
//        IndexResponse response = mock(IndexResponse.class);
//        when(response.toString()).thenReturn("response");
//        IndexRequestBuilder irb = mock(IndexRequestBuilder.class);
//        PoguesItem item = new PoguesItem("foo", "0", "bar");
//        ObjectMapper mapper = new ObjectMapper();
//        byte[] data = mapper.writeValueAsBytes(item);
//        when(irb.setSource(data)).thenReturn(irb);
//        when(irb.get()).thenReturn(response);
//        when(client.prepareIndex(null, "anything", "foo")).thenReturn(irb);
//        Assert.assertEquals(response, repository.save("anything", item));
    }

    @Test
    public void deleteTest() throws Exception {
//        DeleteResponse response = mock(DeleteResponse.class);
//        when(response.toString()).thenReturn("response");
//        DeleteRequestBuilder drb = mock(DeleteRequestBuilder.class);
//        when(drb.get()).thenReturn(response);
//        when(client.prepareDelete(null, "foo", "bar")).thenReturn(drb);
//        Assert.assertEquals(response, repository.delete("foo", "bar"));
    }

    @Test
    public void findByLabelTest() throws Exception {
//        SearchResponse response = mock(SearchResponse.class);
//        SearchRequestBuilder srb = mock(SearchRequestBuilder.class);
//        SearchHits shs = mock(SearchHits.class);
//        SearchHit sh = mock(SearchHit.class);
//        when(client.prepareSearch(null)).thenReturn(srb);
//        when(srb.setTypes("anything")).thenReturn(srb);
//        when(srb.setQuery(QueryBuilders.matchQuery("label", "bar"))).thenReturn(srb);
//        when(srb.get()).thenReturn(response);
//        when(response.getHits()).thenReturn(shs);
//        when(sh.getId()).thenReturn("foo");
//        when(sh.getSource()).thenReturn(new HashMap<String, Object>(){
//            { put("label", "bar"); }
//            { put("parent", "0"); }
//        });
//        when(sh.getType()).thenReturn("anything");
//        when(shs.getHits()).thenReturn(new SearchHit[]{
//           sh
//        });
//        List<DDIItem> hits = repository.findByLabel("bar", "anything");
//        Assert.assertEquals("anything", hits.get(0).getType());
//        Assert.assertEquals("bar", hits.get(0).getLabel());
//        Assert.assertEquals("foo", hits.get(0).getId());
    }
}
