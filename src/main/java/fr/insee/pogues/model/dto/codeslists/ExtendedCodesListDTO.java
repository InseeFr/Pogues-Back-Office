package fr.insee.pogues.model.dto.codeslists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedCodesListDTO extends CodesListDTO {
    private List<String> relatedQuestionNames;

    public ExtendedCodesListDTO(CodesListDTO codesListDTO, List<String> relatedQuestionNames){
        super(codesListDTO.getId(), codesListDTO.getLabel(), codesListDTO.getCodes());
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
