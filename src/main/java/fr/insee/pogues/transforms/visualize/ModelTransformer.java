package fr.insee.pogues.transforms.visualize;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface ModelTransformer {
	ByteArrayOutputStream transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception;

}
