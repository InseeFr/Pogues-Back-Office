package fr.insee.pogues.search.model;

public class PoguesItem {

    public PoguesItem(){ }

    public PoguesItem(String id, String label) {
        this.id = id;
        this.label = label;
    }

    private String id;

    private String label;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
