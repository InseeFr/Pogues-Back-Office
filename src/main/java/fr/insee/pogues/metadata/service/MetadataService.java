package fr.insee.pogues.metadata.service;

import fr.insee.pogues.search.model.Family;
import org.json.simple.JSONObject;

import java.util.List;

public interface MetadataService {

    JSONObject getItem(String id) throws Exception;
    Family getFamily(String id) throws Exception;
    List<String> getFamilyIds() throws Exception;
}
