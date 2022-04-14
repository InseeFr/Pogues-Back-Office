package fr.insee.pogues.transforms;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.insee.pogues.webservice.rest.PoguesException;

class TestPipeline {

    @Test
    void passesThroughTest() throws Exception {
        String input = "";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).transform();
        assertEquals(input, output);
    }

    @Test
    void mapsTest() throws Exception {
        String input = "";
        PipeLine.Transform<String, String> t0 = (String i, Map<String, Object> params,String survey) -> i + "concat";
        PipeLine pipeline = new PipeLine();
        String output = pipeline.from(input).map(t0, null,null).transform();
        assertEquals("concat", output);
    }

    @Test
    void throwsExceptionTest() throws Exception {
        String input = "";
        PipeLine.Transform<String, String> t0 = (String i, Map<String, Object> params,String survey) -> {
            throw new PoguesException(500, "Expected error", "Mapping function exception correctly propagates through pipeline");
        };
        PipeLine pipeline = new PipeLine();
        Throwable exception = assertThrows(RuntimeException.class, () -> pipeline.from(input).map(t0, null,null).transform());
        assertEquals("Exception occured while executing mapping function: Expected error",exception.getMessage());
    }
}
