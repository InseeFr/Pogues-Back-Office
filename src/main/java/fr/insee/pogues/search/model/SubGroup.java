package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"studyUnits"})
public class SubGroup extends PoguesItem {

    List<StudyUnit> studyUnits;

    public SubGroup(){
        this.studyUnits = new ArrayList<>();
    }

    public SubGroup(String id, String parent, String label){
        super(id, parent, label);
        this.studyUnits = new ArrayList<>();
    }

    public List<StudyUnit> getStudyUnits() {
        return studyUnits;
    }

    public void setStudyUnits(List<StudyUnit> studyUnits) {
        this.studyUnits = studyUnits;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[ id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", studyUnits = [ ");
        for(StudyUnit o: studyUnits){
            sb.append(o.toString());
            sb.append(",");
        }
        sb.append(" ] ]");
        return sb.toString();
    }
}
