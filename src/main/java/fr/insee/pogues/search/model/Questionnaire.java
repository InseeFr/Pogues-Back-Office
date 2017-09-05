package fr.insee.pogues.search.model;

public class Questionnaire extends PoguesItem {

    @Override
    public String toString() {
        return "[id=" + getId() + ", label=" + getLabel() + "]";
    }
}
