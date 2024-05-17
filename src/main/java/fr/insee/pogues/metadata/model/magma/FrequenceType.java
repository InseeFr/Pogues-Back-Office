package fr.insee.pogues.metadata.model.magma;

public enum FrequenceType {
    U("Multiannual"),
    P("Punctual or aperiodic"),
    A3("Triennial"),
    M("Monthly"),
    A("Annual"),
    Q("Quarterly"),
    T("Bimonthly"),
    C("Continuous");

    public final String label;

    private FrequenceType(String label) {
        this.label = label;
    }
}
