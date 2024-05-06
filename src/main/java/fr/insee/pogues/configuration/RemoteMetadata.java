package fr.insee.pogues.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("fr.insee.pogues.api.remote.metadata")
public class RemoteMetadata {

    String url;

    String agency;

    String key;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
