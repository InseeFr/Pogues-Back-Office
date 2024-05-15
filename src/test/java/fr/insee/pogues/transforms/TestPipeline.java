package fr.insee.pogues.transforms;

import fr.insee.pogues.webservice.rest.PoguesException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static fr.insee.pogues.transforms.visualize.ModelMapper.inputStream2String;
import static fr.insee.pogues.transforms.visualize.ModelMapper.string2BOAS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestPipeline {

    @Test
    void passesThroughTest() throws Exception {
        byte[] inputBytes = "".getBytes(StandardCharsets.UTF_8);
        InputStream input = new ByteArrayInputStream(inputBytes);
        PipeLine pipeline = new PipeLine();
        ByteArrayOutputStream output = pipeline.from(input).transform();
        assertEquals(new String(inputBytes), new String(output.toByteArray()));
    }

    @Test
    void mapsTest() throws Exception {
        String input = "";
        PipeLine.Transform<InputStream, ByteArrayOutputStream> t0 = (InputStream i, Map<String, Object> params,String survey) -> {
            String resultAsString = inputStream2String(i) + "concat";
            return string2BOAS(resultAsString);
        };
        PipeLine pipeline = new PipeLine();
        ByteArrayOutputStream output = pipeline.from(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)))
                .map(t0, null,null).transform();
        assertEquals("concat", new String(output.toByteArray()));
    }

    @Test
    void throwsExceptionTest() throws Exception {
        String input = "";
        PipeLine.Transform<InputStream, ByteArrayOutputStream> t0 = (InputStream i, Map<String, Object> params,String survey) -> {
            throw new PoguesException(500, "Expected error", "Mapping function exception correctly propagates through pipeline");
        };
        PipeLine pipeline = new PipeLine();
        Throwable exception = assertThrows(RuntimeException.class, () -> pipeline
                .from(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)))
                .map(t0, null,null).transform());
        //assertEquals("Exception occured while executing mapping function: Expected error",exception.getMessage());
        // TODO: to be reviewed
    }
}
