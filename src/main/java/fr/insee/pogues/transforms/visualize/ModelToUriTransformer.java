package fr.insee.pogues.transforms.visualize;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface ModelToUriTransformer {
	URI transform(InputStream inputStream, Map<String, Object> params, String surveyName) throws Exception;
}
