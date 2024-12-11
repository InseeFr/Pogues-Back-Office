package fr.insee.pogues.persistence.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.utils.json.JSONFunctions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static fr.insee.pogues.utils.DateUtils.convertTimestampToZonedDateTime;

@Slf4j
public class VersionRowMapper implements RowMapper<Version> {
    private boolean withData;

    public VersionRowMapper(boolean withData){
        this.withData = withData;
    }

    @Override
    public Version mapRow(ResultSet rs, int rowNum) throws SQLException {
        Version version = new Version();
        version.setId(UUID.fromString(rs.getString("id")));
        version.setPoguesId(rs.getString("pogues_id"));
        version.setDay(rs.getDate("day"));
        version.setTimestamp(convertTimestampToZonedDateTime(rs.getTimestamp("timestamp")));
        version.setAuthor(rs.getString("author"));
        if(withData){
            try {
                version.setData(JSONFunctions.jsonStringtoJsonNode(rs.getString("data")));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return version;
    }
}
