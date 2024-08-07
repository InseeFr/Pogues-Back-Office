package fr.insee.pogues.persistence.impl;

import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.persistence.repository.QuestionnaireVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VersionPostgresql implements QuestionnaireVersionRepository {

	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Value("${feature.database.maxCurrentVersions}")
	private int maxCurrentVersions;
		
	private static final String NOT_FOUND="Not found";
	private static final String FORBIDDEN="Forbidden";

	@Override
	public List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception {
		String columns = withData ? " id, timestamp, pogues_id, day, data " : " id, timestamp, pogues_id, day ";
		String qString =
				"SELECT" + columns +
						"FROM pogues_version pv WHERE pv.pogues_id = ? ORDER BY timestamp DESC;";
		return jdbcTemplate.query(qString,  new VersionRowMapper(withData), poguesId);
	}

	@Override
	public Version getLastVersionByQuestionnaireId(String poguesId) throws Exception {
		return null;
	}

	@Override
	public JsonNode getVersionDataByVersionId(String versionId) throws Exception {
		return null;
	}

	@Override
	public Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception {
		String columns = withData ? " id, timestamp, pogues_id, day, data " : " id, timestamp, pogues_id, day ";
		String qString =
				"SELECT" + columns +
				"FROM pogues_version pv WHERE pv.id = ?;";
		return jdbcTemplate.queryForObject(qString, new VersionRowMapper(withData), versionId);
	}

	@Override
	public void createVersion(Version version) throws Exception {
		String qString = """
				INSERT INTO pogues_version (id, data, "timestamp", "day", pogues_id) 
				VALUES (?, ?, ?, ?, ?);
				
				DELETE from pogues_version pv WHERE pv.id IN
				(SELECT pv.id FROM pogues_version pv
					WHERE pv.pogues_id = ? and pv.day = ?
				    ORDER BY pv."timestamp" desc offset ? );
				    
				DELETE FROM pogues_version
				WHERE pogues_id = ?
				AND day != ?
				AND (day, pogues_id, timestamp) NOT IN (
					SELECT day, pogues_id, MAX(timestamp)
				    FROM pogues_version
				    WHERE pogues_id = ?
				    AND day != ?
				    GROUP BY day, pogues_id
				);
				""";
		PGobject jsonData = new PGobject();
		jsonData.setType("jsonb");
		jsonData.setValue(version.getData().toString());
		jdbcTemplate.update(qString,
				// insert request
				version.getId(), jsonData, version.getTimestamp(), version.getDay(), version.getPoguesId(),
				// Delete request: we keep last ${maxCurrentVersions} for the current day
				version.getPoguesId(), version.getDay(), maxCurrentVersions,
				// Delete request: we keep only the last version for each edited day
				version.getPoguesId(), version.getDay(), version.getPoguesId(), version.getDay()
		);
	}

	@Override
	public void deleteVersionsByQuestionnaireId(String poguesId) throws Exception {
		String qString = "DELETE from pogues_version pv WHERE pv.pogues_id = ?;";
		jdbcTemplate.update(qString, poguesId);
	}
}
