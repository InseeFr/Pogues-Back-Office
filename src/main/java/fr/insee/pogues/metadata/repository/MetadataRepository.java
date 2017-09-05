package fr.insee.pogues.metadata.repository;

import org.json.simple.JSONObject;

public interface MetadataRepository {

    JSONObject findById(String id) throws Exception;
}
