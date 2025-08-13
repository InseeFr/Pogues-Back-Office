package fr.insee.pogues.webservice.model.dtd.nomenclatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedNomenclature extends Nomenclature {
    private List<String> relatedQuestionNames;

    public ExtendedNomenclature(Nomenclature nomenclature, List<String> relatedQuestionNames){
        super(nomenclature.getId(), nomenclature.getLabel(), nomenclature.getVersion(), nomenclature.getExternalLink());
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
