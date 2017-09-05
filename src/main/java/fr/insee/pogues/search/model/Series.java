package fr.insee.pogues.search.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"operations"})
public class Series extends PoguesItem {

    List<Operation> operations;

    public Series(){
        this.operations = new ArrayList<>();
    }

    public Series(String id, String parent, String label){
        super(id, parent, label);
        this.operations = new ArrayList<>();
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[ id = " + getId());
        sb.append(", label = " + getLabel());
        sb.append(", operations = [ ");
        for(Operation o: operations){
            sb.append(o.toString());
            sb.append(",");
        }
        sb.append(" ] ]");
        return sb.toString();
    }
}
