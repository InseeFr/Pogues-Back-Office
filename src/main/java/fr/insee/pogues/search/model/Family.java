package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"series"})
public class Family extends PoguesItem
{

    private List<Series> series;

    public Family(){
        this.series = new ArrayList<>();
    }

    public Family(String label, String id) {
        super(label, null, id);
        this.series = new ArrayList<>();
    }


    public List<Series> getSeries(){
        return this.series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", series = [ ");
        for(Series s: series){
            sb.append(s.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }

}
