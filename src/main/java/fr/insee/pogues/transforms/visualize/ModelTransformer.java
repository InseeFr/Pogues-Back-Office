package fr.insee.pogues.transforms.visualize;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

public interface ModelTransformer {
    // Note: output should be a generic OutputStream object (quite a heavy refactor).
    ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception;

}
