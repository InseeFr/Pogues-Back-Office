package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"questionnaires"})
public class Operation extends PoguesItem {

    List<DataCollection> dataCollections;

    public Operation() {
        dataCollections = new ArrayList<>();
    }

    public Operation(String id, String parent, String label) {
        super(id, parent, label);
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
