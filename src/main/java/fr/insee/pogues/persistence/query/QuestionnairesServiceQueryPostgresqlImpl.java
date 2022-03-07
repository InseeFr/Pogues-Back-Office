package fr.insee.pogues.persistence.query;

import fr.insee.pogues.webservice.rest.PoguesException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Questionnaire Service Query for the Postgresql implementation to assume the
 * persistance of Pogues UI in JSON
 * 
 * @author I6VWID
 *
 */
@Service
public class QuestionnairesServiceQueryPostgresqlImpl implements QuestionnairesServiceQuery {

	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * A method to get the `QuestionnaireList` object in the database
	 * 
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public List<JSONObject> getQuestionnaires() throws Exception {
		List<PGobject> data = jdbcTemplate.queryForList("SELECT data FROM pogues", PGobject.class);
		return PgToJSON(data);
	}

	/**
	 * A method to get the questionnaire with an id
	 * 
	 * @param id id of the questionnaire
	 * @return the JSON description of the questionnaire
	 */
	public JSONObject getQuestionnaireByID(String id) throws Exception {
		try {
			String qString = "SELECT data FROM pogues WHERE id=?";
			PGobject q = jdbcTemplate.queryForObject(qString, new Object[] { id }, PGobject.class);
			return (JSONObject) (new JSONParser().parse(q.toString()));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * A method to get the questionnaire in JSON Lunatic with an id
	 * 
	 * @param id id of the questionnaire
	 * @return the JSON Lunatic description of the questionnaire
	 */
	public JSONObject getJsonLunaticByID(String id) throws Exception {
		try {
			String qString = "SELECT data_lunatic FROM visu_lunatic WHERE id=?";
			PGobject q = jdbcTemplate.queryForObject(qString,
					new Object[]{id}, PGobject.class);
			return (JSONObject) (new JSONParser().parse(q.toString()));
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
		String qString = "DELETE FROM pogues where id=?";
		int r = jdbcTemplate.update(qString, new Object[] { id });
		if (0 == r) {
			throw new PoguesException(404, "Not Found", String.format("Entity with id %s not found", id));
		}
	}
	
	/**
	 * A method to delete the questionnaire in JSON Lunatic with an id
	 * 
	 * @param id id of the questionnaire
	 *            
	 */
	public void deleteJsonLunaticByID(String id) throws Exception {
		String qString = "DELETE FROM visu_lunatic where id=?";
		int r = jdbcTemplate.update(qString,
				new Object[] { id });
		if(0 == r) {
			throw new PoguesException(404, "Not Found", String.format("Entity with id %s not found", id));
		}
	}

	/**
	 * A method to get the `QuestionnaireList` with an owner in the database
	 * 
	 * @param owner stamp of the owner of the questionnaire
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public List<JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
		String qString = "SELECT data FROM pogues WHERE data ->> 'owner' = ?";
		List<PGobject> data = jdbcTemplate.queryForList(qString, new Object[] { owner }, PGobject.class);
		return PgToJSON(data);
	}

	/**
	 * A method to get the metadata of a questionnaire
	 * 
	 * @param owner stamp of the owner of the questionnaire
	 * @return metadata of a questionnaire : id, lastUpdatedDate, label, final, DataCollection and TargetMode
	 */
	public List<JSONObject> getMetaQuestionnaire(String owner) throws Exception {
		String qString = "SELECT CONCAT('{\"id\": ',data -> 'id',', \"lastUpdatedDate\": ', data -> 'lastUpdatedDate',', \"Label\": ', data -> 'Label',', \"final\": ', data -> 'final',', \"DataCollection\": ', data -> 'DataCollection',', \"TargetMode\": ', data -> 'TargetMode','}') "
				+ "FROM pogues WHERE data ->> 'owner' = ? AND data -> 'TargetMode' IS NOT NULL";
		List<PGobject> data = jdbcTemplate.queryForList(qString, new Object[] { owner }, PGobject.class);
		System.out.println(data);
		return PgToJSON(data);
	}

	public List<JSONObject> getStamps() throws Exception {
		String qString = "SELECT DISTINCT CONCAT('{\"id\": ',data -> 'owner',', \"label\": ',data -> 'owner','}')  FROM pogues WHERE data -> 'owner' IS NOT NULL";
		List<PGobject> data = jdbcTemplate.queryForList(qString, PGobject.class);
		return PgToJSON(data);
	}

	/**
	 * A method to save a new questionnaire in database
	 * 
	 * @param questionnaire the JSON description of the questionnaire
	 */
	public void createQuestionnaire(JSONObject questionnaire) throws Exception {
		String qString = "INSERT INTO pogues (id, data) VALUES (?, ?)";
		String id = (String) questionnaire.get("id");
		if (null != getQuestionnaireByID(id)) {
			throw new NonUniqueResultException("Entity already exists");
		}
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toJSONString());
		jdbcTemplate.update(qString, id, q);
	}
	
	
	/**
	 * A method to save a new questionnaire in JSON Lunatic in database
	 * 
	 * @param questionnaireLunatic the JSON Lunatic description of the questionnaire
	 */
	public void createJsonLunatic(JSONObject questionnaireLunatic) throws Exception {
		String qString =
				"INSERT INTO visu_lunatic (id, data_lunatic) VALUES (?, ?)";
	    String id  = (String)questionnaireLunatic.get("id");
		if(null != getJsonLunaticByID(id)){
			throw new NonUniqueResultException("Entity already exists");
		}
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaireLunatic.toJSONString());
		jdbcTemplate.update(qString, id, q);
    }

	/**
	 * A method to update an existing questionnaire in database
	 * 
	 * @param id id of the questionnaire
	 * @param questionnaire the JSON description of the questionnaire
	 */
	public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
		String qString = "UPDATE pogues SET data=? WHERE id=?";
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toJSONString());
		int r = jdbcTemplate.update(qString, q, id);
		if (0 == r) {
			throw new NonUniqueResultException("Entity already exists");
		}
	}
	
	/**
	 * A method to update an existing questionnaire in JSON Lunatic in database
	 * 
	 * @param id id of the questionnaire
	 * @param questionnaireLunatic the JSON Lunatic description of the questionnaire
	 */
	public void updateJsonLunatic(String id, JSONObject questionnaireLunatic) throws Exception {
       	String qString = "UPDATE visu_lunatic SET data_lunatic=? WHERE id=?";
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaireLunatic.toJSONString());
		int r = jdbcTemplate.update(qString, q, id);
		if(0 == r) {
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
		return q.toString();
	}

	private List<JSONObject> PgToJSON(List<PGobject> data) {
		return Objects.requireNonNull(data).stream()
				.map(q -> {
					try {
						return (JSONObject) (new JSONParser().parse(q.toString()));
					} catch (ParseException e) {
						throw new RuntimeException("ERROR parsing Json DATA");
					}
				})
				.collect(Collectors.toList());
	}



}
