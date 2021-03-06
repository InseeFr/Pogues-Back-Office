package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.client.MetadataClient;
import fr.insee.pogues.metadata.client.MetadataClientImpl;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestMetadataClient {

    @Mock
    HttpClientBuilder clientFactory;

    @InjectMocks
    MetadataClient metadataClient;

    @Before
    public void beforeEach(){
        metadataClient = spy(new MetadataClientImpl());
        initMocks(this);
    }

    @Test
    public void testClientGet() throws Exception {
//        ColecticaItem expectedResponse = new ColecticaItem();
//        expectedResponse.setIdentifier("foo");
//        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
//        StatusLine statusLine = mock(StatusLine.class);
//        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
//        when(clientFactory.build())
//                .thenReturn(httpClient);
//        when(statusLine.getStatusCode()).thenReturn(200);
//        when(response.getStatusLine()).thenReturn(statusLine);
//        when(response.getEntity()).thenReturn(/* TODO */);
//        when(httpClient.execute(any())).thenReturn(response);
//        ColecticaItem actualResponse = metadataClient.getItem("foo");
//        assertEquals("foo", actualResponse.getIdentifier());
    }
}
