package fr.insee.pogues.search.model;

public class DDIItem {

    private String type;

    public DDIItem(){
        super();
    }

    public DDIItem(String id, String label, String parent, String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
