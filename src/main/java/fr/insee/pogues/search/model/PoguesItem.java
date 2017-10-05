package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties({"children"})
public class PoguesItem {

    private String id;

    private String label;

    private String parent;

    private String groupId;

    private String subGroupId;

    private String studyUnitId;

    private String dataCollectionId;

    private String resourcePackageId;

    private List<PoguesItem> children;

    public PoguesItem(){ }

    public PoguesItem(String id, String parent,  String label) {
        this.id = id;
        this.label = label;
        this.parent = parent;
    }

    public List<PoguesItem> getChildren() {
        return children;
    }

    public void setChildren(List<PoguesItem> children) {
        this.children = children;
    }

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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getResourcePackageId() {
        return resourcePackageId;
    }

    public void setResourcePackageId(String resourcePackageId) {
        this.resourcePackageId = resourcePackageId;
    }
    
    public String toString(){
    	 return this.id  + "-" + this.label + "-";
    }
}
