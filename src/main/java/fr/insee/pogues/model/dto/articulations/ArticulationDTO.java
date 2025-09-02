package fr.insee.pogues.model.dto.articulations;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * An articulation has various items which allow to display data
 * related to a roundabout.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticulationDTO {
    private List<ArticulationItemDTO> items;
}
