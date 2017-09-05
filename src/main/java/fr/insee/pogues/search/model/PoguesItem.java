package fr.insee.pogues.search.model;

public class PoguesItem {

    public PoguesItem(){ }

    public PoguesItem(String id, String parent,  String label) {
        this.id = id;
        this.label = label;
        this.parent = parent;
    }

    private String id;

    private String label;

    private String parent;

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

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
