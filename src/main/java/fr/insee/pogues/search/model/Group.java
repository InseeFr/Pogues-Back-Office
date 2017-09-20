package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"subGroups"})
public class Group extends PoguesItem
{

    private List<SubGroup> subGroups;

    public Group(){
        this.subGroups = new ArrayList<>();
    }

    public Group(String label, String id) {
        super(label, null, id);
        this.subGroups = new ArrayList<>();
    }


    public List<SubGroup> getSubGroups(){
        return this.subGroups;
    }

    public void setSubGroups(List<SubGroup> subGroups) {
        this.subGroups = subGroups;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", subGroups = [ ");
        for(SubGroup s: subGroups){
            sb.append(s.toString());
            sb.append(",");
        }
        sb.append("]]");
        return sb.toString();
    }

}
