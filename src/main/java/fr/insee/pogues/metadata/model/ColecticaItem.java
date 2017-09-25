package fr.insee.pogues.metadata.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * {
 "VersionRationale": {
 "property1": "string",
 "property2": "string"
 },
 "ItemType": "string",
 "AgencyId": "string",
 "Version": 0,
 "Identifier": "string",
 "Item": "string",
 "Notes": [
 {}
 ],
 "VersionDate": "2017-09-14T09:12:41Z",
 "VersionResponsibility": "string",
 "IsPublished": true,
 "IsDeprecated": true,
 "IsProvisional": true,
 "ItemFormat": "string"
 }
 */
public class ColecticaItem {

    @JsonProperty("ItemType")
    public String itemType;

    @JsonProperty("AgencyId")
    public String agencyId;

    @JsonProperty("Version")
    public String version;

    @JsonProperty("Identifier")
    public String identifier;

    @JsonProperty("Item")
    public String item;

    @JsonProperty("VersionDate")
    public String versionDate;

    @JsonProperty("VersionResponsibility")
    public String versionResponsibility;

    @JsonProperty("IsPublished")
    public boolean isPublished;

    @JsonProperty("IsDeprecated")
    public boolean isDeprecated;

    @JsonProperty("IsProvisional")
    public boolean isProvisional;

    @JsonProperty("ItemFormat")
    public String itemFormat;

    @JsonProperty("VersionRationale")
    public JSONObject versionRationale;

    @JsonProperty("Notes")
    public JSONArray notes;

    /////////


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(String versionDate) {
        this.versionDate = versionDate;
    }

    public String getVersionResponsibility() {
        return versionResponsibility;
    }

    public void setVersionResponsibility(String versionResponsibility) {
        this.versionResponsibility = versionResponsibility;
    }

    public boolean isPublished() {
        return isPublished;
    }

    public void setPublished(boolean published) {
        isPublished = published;
    }

    public boolean isDeprecated() {
        return isDeprecated;
    }

    public void setDeprecated(boolean deprecated) {
        isDeprecated = deprecated;
    }

    public boolean isProvisional() {
        return isProvisional;
    }

    public void setProvisional(boolean provisional) {
        isProvisional = provisional;
    }

    public String getItemFormat() {
        return itemFormat;
    }

    public void setItemFormat(String itemFormat) {
        this.itemFormat = itemFormat;
    }

    public JSONObject getVersionRationale() {
        return versionRationale;
    }

    public void setVersionRationale(JSONObject versionRationale) {
        this.versionRationale = versionRationale;
    }

    public JSONArray getNotes() {
        return notes;
    }

    public void setNotes(JSONArray notes) {
        this.notes = notes;
    }
}
