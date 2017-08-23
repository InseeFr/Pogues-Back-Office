package fr.insee.pogues.transforms;

import java.util.Map;

public interface StromaeService {

   String transform(String input, Map<String, Object> params) throws Exception;
}
