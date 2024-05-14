package fr.insee.pogues.transforms;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.insee.pogues.transforms.visualize.ModelMapper.output2Input;

@Slf4j
public class PipeLine {

    private ByteArrayOutputStream output;
    private final List<Runnable> transforms = new ArrayList<>();

    public PipeLine from(InputStream input) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(input.readAllBytes());
        input.close();
        output = byteArrayOutputStream;
        return this;
    }

    public PipeLine map(Transform<InputStream, ByteArrayOutputStream> t, Map<String, Object> params, String surveyName) throws Exception {
        transforms.add(() -> {
            try {
                InputStream input = output2Input(output);
                output = t.apply(input, params, surveyName);
            } catch (Exception e) {
                log.error(String.format("While applying transform to survey %s with params %s",surveyName, params));
                throw new RuntimeException("Exception occured while executing mapping function ", e);
            }
        });
        return this;
    }


    public ByteArrayOutputStream transform() throws Exception {
        for (Runnable t : transforms) {
            t.run();
        }
        return output;
    }

    /// [marker0]
    @FunctionalInterface
    public interface Transform<I, O> {
        O apply(I i, Map<String, Object> params, String surveyName) throws Exception;
    }
    /// [marker0]

}


