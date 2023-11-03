package fr.insee.pogues.transforms;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipeLine {


    private String output;
    private final List<Runnable> transforms = new ArrayList<>();
    static final Logger logger = LogManager.getLogger(PipeLine.class);

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
                logger.error(String.format("While applying transform to survey %s with params %s",surveyName, params));
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


