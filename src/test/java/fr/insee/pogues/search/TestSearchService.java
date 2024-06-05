package fr.insee.pogues.search;

import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.service.MetadataService;
import fr.insee.pogues.metadata.service.MetadataServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestSearchService {

    @Mock
    MetadataRepository repository;
    @InjectMocks
    MetadataService service;

    @BeforeEach
    public void setUp() {
        service = spy(new MetadataServiceImpl()); // <- class under test
        initMocks(this);
    }



}
