package fr.insee.pogues.persistence.query;

import com.google.common.collect.Lists;
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
	public List<JSONObject> getQuestionnaires() throws Exception{
		List<PGobject> data = jdbcTemplate.queryForList("SELECT data FROM pogues", PGobject.class);
		return PgToJSON(data);
	}

	/**
	 * A method to get the questionnaire with an id
	 * 
	 * @param id
	 *            id of the questionnaire
	 * @return the JSON description of the questionnaire
	 */
	public JSONObject getQuestionnaireByID(String id) throws Exception {
		try {
			String qString = "SELECT data FROM pogues WHERE id=?";
			PGobject q = jdbcTemplate.queryForObject(qString,
					new Object[]{id}, PGobject.class);
			return (JSONObject) (new JSONParser().parse(q.toString()));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	
	/**
	 * A method to get the questionnaire with an id
	 * 
	 * @param id
	 *            id of the questionnaire
	 * @return the JSON description of the questionnaire
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
	 * @param id
	 *            id of the questionnaire
	 */
	public void deleteQuestionnaireByID(String id) throws Exception {
		String qString = "DELETE FROM pogues where id=?";
		int r = jdbcTemplate.update(qString,
				new Object[] { id });
		if(0 == r) {
			throw new PoguesException(404, "Not Found", String.format("Entity with id %s not found", id));
		}
	}
	
	/**
	 * A method to delete the questionnaire with an id
	 * 
	 * @param id
	 *            id of the questionnaire
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
	 * @param owner
	 *            owner of the questionnaire
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
    public List<JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
		String qString =
				"SELECT data FROM pogues WHERE data ->> 'owner' = ?";
    	List<PGobject> data = jdbcTemplate.queryForList(qString,
				new Object[] { owner }, PGobject.class);
		return PgToJSON(data);
    }
    
	public List<JSONObject> getMetaQuestionnaire(String owner) throws Exception{
		String qString = "SELECT CONCAT('{\"id\": ',data -> 'id',', \"lastUpdatedDate\": ', data -> 'lastUpdatedDate',', \"label\": ', data -> 'Label',', \"final\": ', data -> 'final',', \"DataCollection\": ', data -> 'DataCollection',', \"TargetMode\": ', data -> 'TargetMode','}') FROM pogues WHERE data ->> 'owner' = ?";
    	List<PGobject> data = jdbcTemplate.queryForList(qString,
				new Object[] { owner }, PGobject.class);
    	System.out.println(data);
		return PgToJSON(data);
	}
	
	public void createQuestionnaire(JSONObject questionnaire) throws Exception {
		String qString =
				"INSERT INTO pogues (id, data) VALUES (?, ?)";
	    String id  = (String)questionnaire.get("id");
		if(null != getQuestionnaireByID(id)){
			throw new NonUniqueResultException("Entity already exists");
		}
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toJSONString());
		jdbcTemplate.update(qString, id, q);
    }
	
	
	public void createJsonLunatic(JSONObject data_lunatic) throws Exception {
		String qString =
				"INSERT INTO visu_lunatic (id, data_lunatic) VALUES (?, ?)";
	    String id  = (String)data_lunatic.get("id");
		if(null != getJsonLunaticByID(id)){
			throw new NonUniqueResultException("Entity already exists");
		}
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(data_lunatic.toJSONString());
		jdbcTemplate.update(qString, id, q);
    }

    public void updateQuestionnaire(String id, JSONObject questionnaire) throws Exception {
       	String qString = "UPDATE pogues SET data=? WHERE id=?";
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(questionnaire.toJSONString());
		int r = jdbcTemplate.update(qString, q, id);
		if(0 == r) {
			throw new NonUniqueResultException("Entity already exists");
		}
    }
    
    public void updateJsonLunatic(String id, JSONObject dataLunatic) throws Exception {
       	String qString = "UPDATE visu_lunatic SET data_lunatic=? WHERE id=?";
		PGobject q = new PGobject();
		q.setType("json");
		q.setValue(dataLunatic.toJSONString());
		int r = jdbcTemplate.update(qString, q, id);
		if(0 == r) {
			throw new NonUniqueResultException("Entity already exists");
		}
    }

    private List<JSONObject> PgToJSON(List<PGobject> data) {
		return Lists.transform(data, q -> {
			try {
				return (JSONObject)(new JSONParser().parse(q.toString()));
			} catch (ParseException e) {
				throw new RuntimeException("ERROR parsing Json DATA");
			}
		});
	}
    


}
