package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.client.DDIASClient;
import fr.insee.pogues.metadata.model.ddias.Unit;
import fr.insee.pogues.metadata.model.pogues.DataCollection;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.repository.MetadataRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class TestMetadataRepository {

    @Mock
    DDIASClient DDIASClient;

    @InjectMocks
    MetadataRepository metadataRepository;

    @BeforeEach
    public void beforeEach() {
        metadataRepository = spy(new MetadataRepositoryImpl());
        initMocks(this);
    }

    @Test
    void findByIdTest() throws Exception {
        List<Unit> expectedUnits = List.of(new Unit("fake-uri", "fake-label"));
        when(DDIASClient.getUnits()).thenReturn(expectedUnits);
        List<Unit> actualUnits = metadataRepository.getUnits();
        assertEquals(expectedUnits, actualUnits);
    }
}
