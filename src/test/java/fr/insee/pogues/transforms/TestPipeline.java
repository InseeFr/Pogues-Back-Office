package fr.insee.pogues.transforms;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestPipeline {

    @Test
    public void passThroughTest() throws Exception {
        String input = "";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).transform();
        assertEquals(input, output);
    }

    @Test
    public void mappingTest() throws Exception {
        String input = "";
        PipeLine.Transform<String, String> t0 = (String i, Map<String, Object> params) -> i + "concat";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).map(t0, null).transform();
        assertEquals("concat", output);
    }
}
