package fr.insee.pogues.webservice.model.dto.codelists;

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
public class ExtendedCodesList extends CodesList {
    private List<String> relatedQuestionNames;

    public ExtendedCodesList(CodesList codesList, List<String> relatedQuestionNames){
        super(codesList.getId(), codesList.getLabel(), codesList.getCodes());
        this.relatedQuestionNames = relatedQuestionNames;
    }
}
