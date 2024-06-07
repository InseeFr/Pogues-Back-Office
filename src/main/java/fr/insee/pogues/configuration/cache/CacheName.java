package fr.insee.pogues.configuration.cache;

public class CacheName {

    private CacheName() {
        throw new IllegalArgumentException("Utility class");
    }

    public static final String UNITS = "units";
    public static final String SERIES = "series";
    public static final String SERIE = "serie";
    public static final String OPERATIONS = "operations";
    public static final String OPERATION = "operation";
}
