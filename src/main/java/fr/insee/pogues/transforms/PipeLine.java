package fr.insee.pogues.transforms;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipeLine {


    private String output;
    private List<Runnable> transforms = new ArrayList<>();

    public PipeLine from(InputStream input) throws Exception {
        try {
            output = IOUtils.toString(input, Charset.forName("UTF-8"));
            return this;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public PipeLine from(String input) {
        this.output = input;
        return this;
    }

    public PipeLine map(Transform<String, String> t, Map<String, Object> params) {
        transforms.add(() -> {
            try {
                output = t.apply(output, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return this;
    }


    public String transform() {
        for (Runnable t : transforms) {
            t.run();
        }
        return output;
    }


    @FunctionalInterface
    public interface Transform<I, O> {
        O apply(I i, Map<String, Object> params) throws Exception;
    }

}


