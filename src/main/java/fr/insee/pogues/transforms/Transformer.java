package fr.insee.pogues.transforms;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface Transformer {
    void transform(InputStream input, OutputStream output, Map<String, Object> params) throws Exception;
    String transform(InputStream input, Map<String, Object> params) throws Exception;
    String transform(String input, Map<String, Object> params) throws Exception;

}
