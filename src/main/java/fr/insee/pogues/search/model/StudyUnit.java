package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"dataCollections"})
public class StudyUnit extends PoguesItem {

    private List<DataCollection> dataCollections;

    public StudyUnit() {
        dataCollections = new ArrayList<>();
    }

    public StudyUnit(String id, String parent, String label) {
        super(id, parent, label);
        this.dataCollections = new ArrayList<>();
    }

    public List<DataCollection> getDataCollections() {
        return dataCollections;
    }

    public void setDataCollections(List<DataCollection> dataCollections) {
        this.dataCollections = dataCollections;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", questionnaires = [ ");
        for(DataCollection dc: dataCollections){
            sb.append(dc.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }
}
