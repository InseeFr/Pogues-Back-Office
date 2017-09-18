package fr.insee.pogues.transforms;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipeLine {


    private String output;
    private List<Runnable> transforms = new ArrayList<>();

    public PipeLine from(InputStream input) throws Exception {
        output = IOUtils.toString(input, Charset.forName("UTF-8"));
        return this;
    }

    public PipeLine from(String input) {
        this.output = input;
        return this;
    }

    public PipeLine map(Transform<String, String> t, Map<String, Object> params) throws Exception {
        transforms.add(() -> {
            try {
                output = t.apply(output, params);
            } catch (Exception e) {
                throw new RuntimeException(
                        String.format("Exception occured while executing mapping function: %s", e.getMessage())
                );
            }
        });
        return this;
    }


    public String transform() throws Exception {
        for (Runnable t : transforms) {
            try {
                t.run();
            } catch (Exception e) {
                throw e;
            }
        }
        return output;
    }

    /// [marker0]
    @FunctionalInterface
    public interface Transform<I, O> {
        O apply(I i, Map<String, Object> params) throws Exception;
    }
    /// [marker0]

}


