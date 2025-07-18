package fr.insee.pogues.persistence.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import fr.insee.pogues.configuration.auth.security.restrictions.StampsRestrictionsService;
import fr.insee.pogues.configuration.cache.CacheName;
import fr.insee.pogues.exception.PoguesException;
import fr.insee.pogues.persistence.exceptions.NonUniqueResultException;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static fr.insee.pogues.utils.json.JSONFunctions.jsonStringtoJsonNode;

/**
 * Questionnaire Service Query for the Postgresql implementation to assume the persistance of Pogues UI in JSON.
 */
@Service
@Slf4j
public class QuestionnaireRepositoryImpl implements QuestionnaireRepository {

	@Value("${application.stamp.restricted}")
	String stampRestricted; 
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	protected StampsRestrictionsService stampsRestrictionsService;
		
	private static final String NOT_FOUND="Not found";
	private static final String FORBIDDEN="Forbidden";

	/**
	 * A method to get the `QuestionnaireList` object in the database
	 * 
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public List<JsonNode> getQuestionnaires() throws Exception {
		List<PGobject> data = jdbcTemplate.queryForList("SELECT data FROM pogues", PGobject.class);
		return pgToJSON(data);
	}

	/**
	 * A method to get the questionnaire with an id
	 * 
	 * @param id id of the questionnaire
	 * @return the JSON description of the questionnaire
	 */
	public JsonNode getQuestionnaireByID(String id) throws Exception {
		try {
			String qString = "SELECT data FROM pogues WHERE id=?";
			PGobject q = jdbcTemplate.queryForObject(qString, PGobject.class, id);
			return jsonStringtoJsonNode(q.toString());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * A method to delete the questionnaire with an id
	 * 
	 * @param id id of the questionnaire
	 */
	public void deleteQuestionnaireByID(String id) throws Exception {
		JsonNode questionnaire= getQuestionnaireByID(id);
		//Check rights
		if (!isUserAuthorized(questionnaire, "Delete")) {
			log.info("User not authorized to delete questionnaire {}",id);
			throw new PoguesException(403, FORBIDDEN, "Only the owner of the questionnaire can delete it");
		}
		String qString = "DELETE FROM pogues where id=?";
		int r = jdbcTemplate.update(qString, id);
		if (0 == r) {
			throw new PoguesException(404, NOT_FOUND, String.format("Entity with id %s not found", id));
		}
	}

	/**
	 * A method to get the `QuestionnaireList` with an owner in the database
	 * 
	 * @param owner stamp of the owner of the questionnaire
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public List<JsonNode> getQuestionnairesByOwner(String owner) throws Exception {
		String qString = "SELECT data FROM pogues WHERE data ->> 'owner' = ?";
		List<PGobject> data = jdbcTemplate.queryForList(qString, PGobject.class, owner);
		return pgToJSON(data);
	}

	/**
	 * A method to get the metadata of a questionnaire
	 * 
	 * @param owner stamp of the owner of the questionnaire
	 * @return metadata of a questionnaire : id, lastUpdatedDate, label, final, DataCollection and TargetMode
	 */
	public List<JsonNode> getMetaQuestionnaire(String owner) throws Exception {
		String qString ="""
				SELECT
					jsonb_build_object(
						'id', (data::jsonb)['id'],
						'Name', (data::jsonb)['Name'],
						'lastUpdatedDate', (data::jsonb)['lastUpdatedDate'],
						'Label', (data::jsonb)['Label'],
						'final', (data::jsonb)['final'],
						'DataCollection', (data::jsonb)['DataCollection'],
						'TargetMode', (data::jsonb)['TargetMode'],
						'flowLogic', (data::jsonb)['flowLogic'],
						'formulasLanguage', (data::jsonb)['formulasLanguage'])
				FROM pogues WHERE (data ->> 'owner') = ? 
					AND (data::jsonb)['Name'] IS NOT NULL
					AND (data::jsonb)['TargetMode'] IS NOT NULL
					AND (data::jsonb)['flowLogic'] IS NOT NULL
					AND (data::jsonb)['formulasLanguage'] IS NOT NULL""";
		List<PGobject> data = jdbcTemplate.queryForList(qString, PGobject.class, owner);
		return pgToJSON(data);
	}

	@Cacheable(CacheName.STAMPS)
	public List<JsonNode> getStamps() throws Exception {
		String qString = """
				SELECT jsonb_build_object('id', owner, 'label', owner) FROM
					(SELECT DISTINCT
						data ->> 'owner' as owner
					FROM pogues WHERE (data ->> 'owner') IS NOT NULL) as owner_table;
				""";
		List<PGobject> data = jdbcTemplate.queryForList(qString, PGobject.class);
		return pgToJSON(data);
	}

	/**
	 * A method to save a new questionnaire in database
	 * 
	 * @param questionnaire the JSON description of the questionnaire
	 */
	public void createQuestionnaire(JsonNode questionnaire) throws Exception {
		String qString = "INSERT INTO pogues (id, data) VALUES (?, ?)";
		String id = questionnaire.get("id").asText();
		if (null != getQuestionnaireByID(id)) {
			throw new NonUniqueResultException("Entity already exists");
		}
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toString());
		jdbcTemplate.update(qString, id, q);
	}

	/**
	 * A method to update an existing questionnaire in database
	 * 
	 * @param id id of the questionnaire
	 * @param questionnaire the JSON description of the questionnaire
	 * @throws SQLException 
	 */
	public void updateQuestionnaire(String id, JsonNode questionnaire) throws Exception {
		//Check rights
		if (!isUserAuthorized(questionnaire, "Update")) {
			log.info("User not authorized to modify questionnaire {}", id);
			throw new PoguesException(403, FORBIDDEN, "Only the owner of the questionnaire can modify it");
		}
		//If permitted, do the update
		String qString = "UPDATE pogues SET data=? WHERE id=?";
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toString());
		int r = jdbcTemplate.update(qString, q, id);
		if (0 == r) {
			throw new NonUniqueResultException("Entity already exists");
		}
	}

	/**
	 * A method to count the questionnaires stored in database
	 */
	@Override
	public String countQuestionnaires() throws Exception {
		String qString = "SELECT count(*) FROM pogues";
		PGobject q = jdbcTemplate.queryForObject(qString, PGobject.class);
		if (q != null) {
			return q.toString();
		} else {
			throw new PoguesException(404, NOT_FOUND, "No questionnaires found in database");
		}
	}

	private List<JsonNode> pgToJSON(List<PGobject> data) {
		return Objects.requireNonNull(data).stream()
				.map(q -> {
					try {
						return jsonStringtoJsonNode(q.toString());
					} catch (JsonProcessingException e) {
						throw new RuntimeException("ERROR parsing Json DATA");
                    }
                })
				.collect(Collectors.toList());
	}
	
	private boolean isStampRestricted(String stamp) {
		return stamp.equals(stampRestricted);
	}
	
	private boolean isUserAuthorized(JsonNode questionnaire, String action) {
		boolean isAuthorized=true;
		String stamp = questionnaire.get("owner").asText();
		if (isStampRestricted(stamp) && !stampsRestrictionsService.isQuestionnaireOwner(stamp)) {
			isAuthorized=false;
		}
		log.info("{} questionnaire {}",action, isAuthorized ? "authorized": "forbidden");
		return isAuthorized;
	}

}
