package fr.insee.pogues.webservice.model.dtd.nomenclatures;


import com.fasterxml.jackson.annotation.JsonInclude;
import fr.insee.pogues.model.SuggesterParametersType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Nomenclature {
    private String id;
    private String label;
    private String version;
    private ExternalLink externalLink;
}
