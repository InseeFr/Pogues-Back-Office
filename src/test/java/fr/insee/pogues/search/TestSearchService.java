package fr.insee.pogues.search;

import fr.insee.pogues.search.repository.PoguesItemRepository;
import fr.insee.pogues.search.service.SearchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestSearchService {

    @Mock
    PoguesItemRepository repository;
    @InjectMocks
    SearchServiceImpl service;

    @BeforeEach
    public void setUp() {
        service = spy(new SearchServiceImpl()); // <- class under test
        initMocks(this);
    }



}
