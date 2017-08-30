package fr.insee.pogues.search.model;

public class PoguesHit extends PoguesItem {

    private String type;

    public PoguesHit(){
        super();
    }

    public PoguesHit(String id, String label, String type) {
        super(id, label);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
