package fr.insee.pogues.metadata;

import fr.insee.pogues.metadata.client.MetadataClient;
import fr.insee.pogues.metadata.client.MetadataClientImpl;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestMetadataClient {

    @Mock
    HttpClient httpClient;

    @InjectMocks
    MetadataClient metadataClient;

    @Before
    public void beforeEach(){
        metadataClient = spy(new MetadataClientImpl());
        initMocks(this);
    }

    @Test
    public void testClientGet() throws Exception {
        String expectedResponse = "{\"id\":\"foo\"}";
        HttpResponse response = mock(HttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(new StringEntity(expectedResponse));
        when(httpClient.execute(any())).thenReturn(response);
        JSONObject actualResponse = metadataClient.getItem("foo");
        assertEquals("foo", actualResponse.get("id").toString());
    }
}
