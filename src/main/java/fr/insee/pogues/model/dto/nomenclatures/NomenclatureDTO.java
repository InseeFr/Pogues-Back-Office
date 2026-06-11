package fr.insee.pogues.model.dto.nomenclatures;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NomenclatureDTO {
    private String id;
    /**
     * @deprecated name is always equal to id, remove name when Pogues-UI and Eno use id attribute instead
     */
    @Deprecated(forRemoval = true)
    private String name;
    private String label;
    private String version;
    private String urn;
    // abstract suggesterParams
    private Object suggesterParameters;
}
