package fr.insee.pogues.transforms;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TestXFormToURI {


    @Mock
    StromaeService stromaeService;

    @InjectMocks
    XFormToURI xformToURI;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp(){
        xformToURI = spy(new XFormToURIImpl());
        initMocks(this);
    }

    @Test
    public void getUriTest(){
        Map<String, Object> params = new HashMap<>();
        params.put("name", "simpsons");
        String input = "<xhtml:html></xhtml>";
        String output = String.format("http://visualization.uri.insee.fr/%s", params.get("name"));
        try {
            when(stromaeService.transform(input, params))
                    .thenReturn(output);
            String uri = xformToURI.transform(input, params);
            assertEquals(uri, output);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
