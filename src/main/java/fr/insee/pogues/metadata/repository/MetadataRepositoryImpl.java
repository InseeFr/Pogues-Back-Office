package fr.insee.pogues.metadata.repository;

import fr.insee.pogues.metadata.client.MetadataClient;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

    @Autowired
    MetadataClient metadataClient;

    @Override
    public JSONObject findById(String id) throws Exception {
        return metadataClient.getItem(id);
    }
}
