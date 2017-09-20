package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"instrumentSchemes"})
public class DataCollection extends PoguesItem {

    private List<InstrumentScheme> instrumentSchemes;

    public DataCollection(){
        this.instrumentSchemes = new ArrayList<>();
    }

    public DataCollection(String label, String parent, String id) {
        super(label, parent, id);
        this.instrumentSchemes = new ArrayList<>();
    }


    public List<InstrumentScheme> getInstrumentSchemes(){
        return this.instrumentSchemes;
    }

    public void setInstrumentSchemes(List<InstrumentScheme> questionnaires) {
        this.instrumentSchemes = questionnaires;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", series = [ ");
        for(InstrumentScheme i: instrumentSchemes){
            sb.append(i.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }

}
