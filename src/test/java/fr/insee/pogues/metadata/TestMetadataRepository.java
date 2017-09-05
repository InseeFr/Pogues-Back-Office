package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.client.MetadataClient;
import fr.insee.pogues.metadata.repository.MetadataRepository;
import fr.insee.pogues.metadata.repository.MetadataRepositoryImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;


public class TestMetadataRepository {

    @Mock
    MetadataClient metadataClient;

    @InjectMocks
    MetadataRepository metadataRepository;

    @Before
    public void beforeEach() {
        metadataRepository = spy(new MetadataRepositoryImpl());
        initMocks(this);
    }

    @Test
    public void findByIdTest() throws Exception {
        String id = "foo";
        JSONObject expectedOutput = new JSONObject();
        expectedOutput.put("id", id);
        when(metadataClient.getItem(id)).thenReturn(expectedOutput);
        JSONObject actualOutput = metadataRepository.findById(id);
        Assert.assertEquals(id, actualOutput.get("id").toString());
    }
}
