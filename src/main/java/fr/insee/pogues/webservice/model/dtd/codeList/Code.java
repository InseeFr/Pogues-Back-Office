package fr.insee.pogues.webservice.model.dtd.codeList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    private String value;
    private String label;
    private List<Code> codes;

}
