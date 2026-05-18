package fr.insee.pogues.domain.entity.db;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Version {

    private UUID id;
    private String poguesId;
    private ZonedDateTime timestamp;
    private Date day;
    private JsonNode data;
    private String author;
}
