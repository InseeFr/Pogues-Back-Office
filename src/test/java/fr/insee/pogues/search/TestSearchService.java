package fr.insee.pogues.search;

import fr.insee.pogues.search.model.PoguesHit;
import fr.insee.pogues.search.repository.PoguesItemRepository;
import fr.insee.pogues.search.service.SearchServiceImpl;
import fr.insee.pogues.webservice.rest.PoguesException;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestSearchService {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Mock
    PoguesItemRepository repository;
    @InjectMocks
    SearchServiceImpl service;

    @Before
    public void setUp() {
        service = spy(new SearchServiceImpl()); // <- class under test
        initMocks(this);
    }

    @Test
    public void emptyResult() throws Exception {
        when(repository.findByLabel("notfound", new String[]{"questionnaire"}))
                .thenReturn(new ArrayList<PoguesHit>());
        Assert.assertEquals(0, service.searchByLabel("notfound", new String[]{"questionnaire"}).size());
    }

    @Test
    public void oneResult() throws Exception {
        PoguesHit hit = new PoguesHit("1", "foo", "0", "questionnaire");
        when(repository.findByLabel("foo", new String[]{"questionnaire"}))
                .thenReturn(new ArrayList<PoguesHit>() {
                    {
                        add(hit);
                    }
                });
        Assert.assertEquals(hit, service.searchByLabel("foo", new String[]{"questionnaire"}).get(0));
    }

    @Test
    public void propagateException() throws Exception {
        exception.expect(PoguesException.class);
        exception.expectMessage("Expected Error");
        PoguesHit hit = new PoguesHit("1", "foo", "0", "questionnaire");
        PoguesException exception = new PoguesException(500, "Expected Error", "This Error Should Propagate To Service Caller");
        when(repository.save("questionnaire", hit))
                .thenThrow(exception);
        service.save("questionnaire", hit);
    }

    @Test
    public void returnsIndexResponse() throws Exception {
        IndexResponse response = Mockito.mock(IndexResponse.class);
        when(response.toString()).thenReturn("response");
        PoguesHit hit = new PoguesHit("1", "foo", "0", "questionnaire");
        when(repository.save("questionnaire", hit))
                .thenReturn(response);
        Assert.assertEquals(response, service.save("questionnaire", hit));
    }
    @Test
    public void returnsDeleteResponse() throws Exception {
        DeleteResponse response = Mockito.mock(DeleteResponse.class);
        when(response.toString()).thenReturn("response");
        when(repository.delete("questionnaire", "foo"))
                .thenReturn(response);
        Assert.assertEquals(response, service.delete("questionnaire", "foo"));
    }

}
