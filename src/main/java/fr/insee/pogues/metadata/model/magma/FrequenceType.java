package fr.insee.pogues.metadata.model.magma;

public enum FrequenceType {
    U("Multiannual"),
    P("Punctual or aperiodic"),
    A3("Triennial"),
    M("Monthly"),
    A("Annual"),
    B("Daily - business week"),
    D("Daily"),
    H("Hourly"),
    I("Biennial"),
    N("Minutely"),
    L("Sub-annual"),
    S("Half-yearly, semester"),
    Q("Quarterly"),
    T("Bimonthly"),
    C("Continuous"),
    W("Weekly");

    public final String label;

    private FrequenceType(String label) {
        this.label = label;
    }
}
