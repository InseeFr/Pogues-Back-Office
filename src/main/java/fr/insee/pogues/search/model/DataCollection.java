package fr.insee.pogues.search.model;

import java.util.ArrayList;
import java.util.List;

public class DataCollection extends PoguesItem {

    private List<Questionnaire> questionnaires;

    public DataCollection(){
        this.questionnaires = new ArrayList<>();
    }

    public DataCollection(String label, String id) {
        super(label, null, id);
        this.questionnaires = new ArrayList<>();
    }


    public List<Questionnaire> getSeries(){
        return this.questionnaires;
    }

    public void setSeries(List<Questionnaire> questionnaires) {
        this.questionnaires = questionnaires;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", series = [ ");
        for(Questionnaire q: questionnaires){
            sb.append(q.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }
}
