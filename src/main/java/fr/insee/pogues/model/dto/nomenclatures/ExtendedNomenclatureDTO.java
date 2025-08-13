package fr.insee.pogues.model.dto.nomenclatures;

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
public class ExtendedNomenclatureDTO extends NomenclatureDTO {
    private List<String> relatedQuestionNames;

    public ExtendedNomenclatureDTO(NomenclatureDTO nomenclatureDTO, List<String> relatedQuestionNames){
        super(nomenclatureDTO.getId(), nomenclatureDTO.getLabel(), nomenclatureDTO.getVersion(), nomenclatureDTO.getExternalLink());
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
