package fr.insee.pogues.webservice.model.dtd.codeList;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedCodesList extends CodesList {
    private List<String> relatedQuestionsId;

    public ExtendedCodesList(CodesList codesList, List<String> relatedQuestionsId){
        super(codesList.getId(), codesList.getLabel(), codesList.getCodes());
        this.relatedQuestionsId = relatedQuestionsId;
    }
}
