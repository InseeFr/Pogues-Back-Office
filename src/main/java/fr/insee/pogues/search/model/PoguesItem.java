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

    private String subGroupId;

    private String studyUnitId;

    private String dataCollectionId;

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

    public String getSubGroupId() {
        return subGroupId;
    }

    public void setSubGroupId(String id) {
        this.subGroupId = id;
    }

    public String getStudyUnitId() {
        return studyUnitId;
    }

    public void setStudyUnitId(String id) {
        this.studyUnitId = id;
    }

    public String getDataCollectionId() {
        return dataCollectionId;
    }

    public void setDataCollectionId(String id) {
        this.dataCollectionId = id;
    }
}
