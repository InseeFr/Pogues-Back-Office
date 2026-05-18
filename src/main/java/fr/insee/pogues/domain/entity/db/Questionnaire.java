package fr.insee.pogues.domain.entity.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Questionnaire {
    private String id;
    private JsonNode data;
}
