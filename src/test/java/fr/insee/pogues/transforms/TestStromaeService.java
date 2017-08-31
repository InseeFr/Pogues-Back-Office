package fr.insee.pogues.transforms;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestStromaeService {

    @Mock
    HttpClient client;

    @Before
    public void setUp(){
        initMocks(this);
    }

    @Test
    public void transformTest() throws IOException {
        HttpResponse response = mock(HttpResponse.class);
        when(response.getEntity()).thenReturn(new StringEntity("http://generated.form.tld"));
        when(client.execute(any()))
                .thenReturn(response);
    }
}
