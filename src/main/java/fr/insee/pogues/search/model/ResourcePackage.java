package fr.insee.pogues.search.model;

import java.util.HashMap;
import java.util.Map;

public class ResourcePackage {

    private String id;
    private Map<String, String> references;

    public ResourcePackage(){
        this.references = new HashMap<>();
    }

    public ResourcePackage(String id) {
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(Map<String, String> references) {
        this.references = references;
    }
}
