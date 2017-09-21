package fr.insee.pogues.metadata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * {
 *  "Identifiers": [
 *          {
 *              "AgencyId": "string",
 *              "Identifier": "string",
 *              "Version": 0
 *          }
 *      ]
 *  }
 */
public class ColecticaItemRefList {

    public ColecticaItemRefList() { }

    public ColecticaItemRefList(List<ColecticaItemRef> identifiers) {
        this.identifiers = identifiers;
    }

    @JsonProperty("Identifiers")
    public List<ColecticaItemRef> identifiers;


}
