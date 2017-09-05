package fr.insee.pogues.metadata.client;

import org.json.simple.JSONObject;

public interface MetadataClient {

    JSONObject getItem(String id) throws Exception;
}
