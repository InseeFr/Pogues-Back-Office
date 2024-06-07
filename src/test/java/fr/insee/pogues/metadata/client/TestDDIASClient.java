package fr.insee.pogues.metadata.client;

import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

class TestDDIASClient {

    @Mock
    HttpClientBuilder clientFactory;

    @InjectMocks
    DDIASClient DDIASClient;

    @BeforeEach
    public void beforeEach(){
        DDIASClient = spy(new DDIASClientImpl());
        initMocks(this);
    }

    @Test
    void testClientGet() throws Exception {
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
