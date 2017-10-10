package fr.insee.pogues.search;

import fr.insee.pogues.search.repository.PoguesItemRepository;
import fr.insee.pogues.search.service.SearchServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
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



}
