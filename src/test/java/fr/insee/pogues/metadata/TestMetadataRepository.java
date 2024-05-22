package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.client.MetadataClient;
import fr.insee.pogues.metadata.model.ColecticaItem;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.repository.MetadataRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


class TestMetadataRepository {

    @Mock
    MetadataClient metadataClient;

    @InjectMocks
    MetadataRepository metadataRepository;

    @BeforeEach
    public void beforeEach() {
        metadataRepository = spy(new MetadataRepositoryImpl());
        initMocks(this);
    }

    @Test
    void findByIdTest() throws Exception {
        ColecticaItem expected = new ColecticaItem();
        expected.setIdentifier("foo");
        when(metadataClient.getItem(expected.getIdentifier())).thenReturn(expected);
        ColecticaItem actual = metadataRepository.findById(expected.getIdentifier());
        assertEquals(expected.getIdentifier(), actual.getIdentifier());
    }
}
