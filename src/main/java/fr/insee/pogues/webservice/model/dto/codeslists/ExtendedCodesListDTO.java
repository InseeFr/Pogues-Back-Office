package fr.insee.pogues.webservice.model.dto.codeslists;

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
public class ExtendedCodesListDTO extends CodesListDTO {
    private List<String> relatedQuestionNames;

    public ExtendedCodesListDTO(CodesListDTO codesListDTO, List<String> relatedQuestionNames){
        super(codesListDTO.getId(), codesListDTO.getLabel(), codesListDTO.getCodes());
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
