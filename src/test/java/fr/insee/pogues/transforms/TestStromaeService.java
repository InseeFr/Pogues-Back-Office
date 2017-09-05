package fr.insee.pogues.transforms;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestStromaeService {

    @Mock
    HttpClient client;

    @InjectMocks
    StromaeService service;

    @Before
    public void setUp(){
        service = spy(new StromaeServiceImpl());
        initMocks(this);
    }

    @Test
    public void transformTest() throws Exception {
        HttpResponse response = mock(HttpResponse.class);
        String expectedURL = "http://generated.form.tld/simpsons";
        Map<String, Object> params = new HashMap<String, Object>(){
            { put("name", "simpsons"); }
        };
        when(response.getEntity()).thenReturn(new StringEntity(expectedURL));
        when(client.execute(any()))
                .thenReturn(response);
        String actualURL = service.transform("<xhtml:html><xhtml:head></xhtml:head></xhtml:html>", params);
        Assert.assertEquals(expectedURL, actualURL);
    }
}
