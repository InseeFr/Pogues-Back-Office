package fr.insee.pogues.search.model;

public class Instrument extends PoguesItem {

    @Override
    public String toString() {
        return "[id=" + getId() + ", label=" + getLabel() + "]";
    }


}
