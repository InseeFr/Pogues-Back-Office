package fr.insee.pogues.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("application.metadata")
public class RemoteMetadata {

    String ddiAs;

    String agency;

    String key;

    public String getDdiAs() {
        return ddiAs;
    }

    public void setDdiAs(String ddiAs) {
        this.ddiAs = ddiAs;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
