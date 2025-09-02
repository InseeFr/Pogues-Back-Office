package fr.insee.pogues.persistence.repository;

import fr.insee.pogues.domain.entity.db.Version;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.exception.VersionNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static fr.insee.pogues.utils.DateUtils.convertZonedDateTimeToTimestamp;

@Service
@Slf4j
public class QuestionnaireVersionRepositoryImpl implements QuestionnaireVersionRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Value("${feature.database.rollingBackup.maxByQuestionnaire}")
	private int maxBackupByQuestionnaire;

	private static final String BASE_COLUMNS = "id, timestamp, pogues_id, day, author";
	private static final String COLUMNS_WITHOUT_DATA = "id, timestamp, pogues_id, day, author";
	private static final String COLUMNS_WITH_DATA = COLUMNS_WITHOUT_DATA + ", data";

	private static final String SELECT_VERSIONS_QUERY = "SELECT %s FROM pogues_version pv WHERE pv.pogues_id = ? ORDER BY timestamp DESC";
	private static final String SELECT_LAST_VERSION_QUERY = "SELECT %s FROM pogues_version pv WHERE pv.pogues_id = ? ORDER BY timestamp DESC LIMIT 1;";
	private static final String SELECT_VERSION_QUERY = "SELECT %s FROM pogues_version pv WHERE pv.id = ?;";

	private static final String SELECT_VERSIONS_QUERY_WITH_DATA = String.format(SELECT_VERSIONS_QUERY, COLUMNS_WITH_DATA);
	private static final String SELECT_VERSIONS_QUERY_WITHOUT_DATA = String.format(SELECT_VERSIONS_QUERY, BASE_COLUMNS);
	private static final String SELECT_LAST_VERSION_QUERY_WITH_DATA = String.format(SELECT_LAST_VERSION_QUERY, COLUMNS_WITH_DATA);
	private static final String SELECT_LAST_VERSION_QUERY_WITHOUT_DATA = String.format(SELECT_LAST_VERSION_QUERY, BASE_COLUMNS);
	private static final String SELECT_VERSION_QUERY_WITH_DATA = String.format(SELECT_VERSION_QUERY, COLUMNS_WITH_DATA);
	private static final String SELECT_VERSION_QUERY_WITHOUT_DATA = String.format(SELECT_VERSION_QUERY, BASE_COLUMNS);

	@Override
	public List<Version> getVersionsByQuestionnaireId(String poguesId, boolean withData) throws Exception {
		String qString = withData ? SELECT_VERSIONS_QUERY_WITH_DATA : SELECT_VERSIONS_QUERY_WITHOUT_DATA;
		List<Version> versions = jdbcTemplate.query(qString,  new VersionRowMapper(withData), poguesId);
		if(versions.isEmpty()){
			throw new PoguesException(404, "Not found", "No version for poguesId "+ poguesId);
		}
		return versions;
	}

	@Override
	public Version getLastVersionByQuestionnaireId(String poguesId, boolean withData) throws Exception {
		String qString = withData ? SELECT_LAST_VERSION_QUERY_WITH_DATA : SELECT_LAST_VERSION_QUERY_WITHOUT_DATA;
		try {
			return jdbcTemplate.queryForObject(qString,  new VersionRowMapper(withData), poguesId);
		} catch (EmptyResultDataAccessException e) {
			throw new PoguesException(404, "Not found", "No version for poguesId "+ poguesId);
		}
	}

	@Override
	public Version getVersionByVersionId(UUID versionId, boolean withData) throws Exception {
		String qString = withData ? SELECT_VERSION_QUERY_WITH_DATA : SELECT_VERSION_QUERY_WITHOUT_DATA;
		try {
			return jdbcTemplate.queryForObject(qString, new VersionRowMapper(withData), versionId);
		} catch (EmptyResultDataAccessException e) {
			String message = String.format("Version with id %s does not exist", versionId);
			throw new VersionNotFoundException(message);
		}
	}

	@Override
	public void cleanVersions() {
		Date nowSqlDate = new Date(Instant.now().toEpochMilli());
		String qString = "CALL clean_versions(?, ?)";
		jdbcTemplate.update(qString, nowSqlDate, maxBackupByQuestionnaire);
	}

	@Override
	public void createVersion(Version version) throws Exception {
		String qString = """
				INSERT INTO pogues_version (id, data, "timestamp", "day", pogues_id, author) 
				VALUES (?, ?, ?, ?, ?, ?);
				""";
		PGobject jsonData = new PGobject();
		jsonData.setType("jsonb");
		jsonData.setValue(version.getData().toString());
		jdbcTemplate.update(qString,
				// insert request
				version.getId(), jsonData, convertZonedDateTimeToTimestamp(version.getTimestamp()), version.getDay(), version.getPoguesId(), version.getAuthor()
		);
	}

	@Override
	public void deleteVersionsByQuestionnaireId(String poguesId) throws Exception {
		String qString = "DELETE from pogues_version pv WHERE pv.pogues_id = ?;";
		int nbVersionsDeleted = jdbcTemplate.update(qString, poguesId);
		if(nbVersionsDeleted == 0) throw new PoguesException(404, "Not found", "No version to delete for poguesId "+ poguesId);
	}

	@Override
	public void deleteAllVersionsByQuestionnaireIdExceptLast(String poguesId) throws Exception {
		String qString = """				
				DELETE from pogues_version pv WHERE pv.id IN 
				(SELECT pv.id FROM pogues_version pv WHERE pv.pogues_id = ? ORDER BY pv."timestamp" desc offset 1 );
				""";
		int nbVersionsDeleted = jdbcTemplate.update(qString, poguesId);
		if(nbVersionsDeleted == 0) log.info("No version to delete for poguesId "+ poguesId);
	}
}
