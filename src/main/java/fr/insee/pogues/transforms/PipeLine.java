package fr.insee.pogues.transforms;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class PipeLine {


    private String output;
    private final List<Runnable> transforms = new ArrayList<>();

    public PipeLine from(InputStream input) throws Exception {
        output = IOUtils.toString(input, StandardCharsets.UTF_8);
        return this;
    }

    public PipeLine from(String input) {
        this.output = input;
        return this;
    }

    public PipeLine map(Transform<String, String> t, Map<String, Object> params, String surveyName) throws Exception {
        transforms.add(() -> {
            try {
                output = t.apply(output, params, surveyName);
            } catch (Exception e) {
                log.error(String.format("While applying transform to survey %s with params %s",surveyName, params));
                throw new RuntimeException("Exception occured while executing mapping function ", e);
            }
        });
        return this;
    }


    public String transform() throws Exception {
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


