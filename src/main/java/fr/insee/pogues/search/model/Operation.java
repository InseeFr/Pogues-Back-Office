package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"questionnaires"})
public class Operation extends PoguesItem {

    List<Questionnaire> questionnaires;

    public Operation() {
        questionnaires = new ArrayList<>();
    }

    public Operation(String id, String parent, String label) {
        super(id, parent, label);
    }

    public List<Questionnaire> getQuestionnaires() {
        return questionnaires;
    }

    public void setQuestionnaires(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", questionnaires = [ ");
        for(Questionnaire o: questionnaires){
            sb.append(o.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }
}
