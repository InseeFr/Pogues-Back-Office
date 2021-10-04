package fr.insee.pogues.transforms;

import fr.insee.pogues.webservice.rest.PoguesException;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestPipeline {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @org.junit.jupiter.api.Test
    public void passesThroughTest() throws Exception {
        String input = "";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).transform();
        assertEquals(input, output);
    }

    @Test
    public void mapsTest() throws Exception {
        String input = "";
        PipeLine.Transform<String, String> t0 = (String i, Map<String, Object> params,String survey) -> i + "concat";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).map(t0, null,null).transform();
        assertEquals("concat", output);
    }

    @Test
    public void throwsExceptionTest() throws Exception {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Exception occured while executing mapping function: Expected error");
        String input = "";
        PipeLine.Transform<String, String> t0 = (String i, Map<String, Object> params,String survey) -> {
            throw new PoguesException(500, "Expected error", "Mapping function exception correctly propagates through pipeline");
        };
        PipeLine pipeline = new PipeLine();
        pipeline.from(input).map(t0, null,null).transform();
    }
}
