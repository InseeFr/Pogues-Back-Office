package fr.insee.pogues.persistence.query;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Questionnaire Service Query for the Postgresql implementation to assume the
 * persistance of Pogues UI in JSON
 * 
 * @author I6VWID
 *
 */
public class QuestionnairesServiceQueryPostgresqlImpl implements QuestionnairesServiceQuery {

	final static Logger logger = Logger.getLogger(QuestionnairesServiceQueryPostgresqlImpl.class);

	private Connection connection = null;

	private Statement stmt = null;

	private PreparedStatement prepStmt = null;

	private ResultSet rs = null;

	private final JSONParser jsonParser = new JSONParser();
	/**
	 * Contructor for Questionnaire Service Query Postrgesql implementation,
	 * init the connection.
	 * 
	 */
	public QuestionnairesServiceQueryPostgresqlImpl() {

		try {
			init();
		} catch (ClassNotFoundException e) {
			logger.error("Postgresql Driver not found");
			e.printStackTrace();
		} catch (SQLException e) {
			logger.error("SQLException - Impossible to init the connection");
			e.printStackTrace();
		}

	}

	/**
	 * A method to init the connection to the database.
	 * 
	 */
	private void init() throws SQLException, ClassNotFoundException {

		Class.forName("org.postgresql.Driver");
		// TODO externalisation of the parameter
		connection = DriverManager.getConnection(
				"jdbc:postgresql://dvrmspogfoldb01.ad.insee.intra:1983/di_pg_rmspogfo_dv01", "user_rmspogfo_loc",
				"rmeS6789");
		stmt = connection.createStatement();
	}


	/**
	 * A method to close the connection to the database.
	 * 
	 */
	public void close() {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("SQLException - Impossible to close the ResultSet");
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				logger.error("SQLException - Impossible to close the Statement");
				e.printStackTrace();
			}
		}
		if (prepStmt != null) {
			try {
				prepStmt.close();
			} catch (SQLException e) {
				logger.error("SQLException - Impossible to close the PreparedStatement");
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("SQLException - Impossible to close the Connection");
				e.printStackTrace();
			}
		}

	}

	/**
	 * A method to get the `QuestionnaireList` object in the database
	 * 
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public Map<String, JSONObject> getQuestionnaires() throws Exception{

		Map<String, JSONObject> questionnaires = new HashMap<String, JSONObject>();
		try {
			rs = stmt.executeQuery("SELECT * FROM pogues");
			while (rs.next()) {
				questionnaires.put(rs.getString(1), (JSONObject)jsonParser.parse(rs.getString(2)));
			}
		} catch (SQLException e) {
			logger.error("SQLException");
			throw e;
		} catch(ParseException e){
			logger.error("Parser Exception");
			throw e;
		}
		/* TODO: add integrity constraints to prohibit creating objects without ID
		 *  -> Then remove next line
		 */
        questionnaires.keySet().removeIf(Objects::isNull);
		return questionnaires;

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
			prepStmt = connection.prepareStatement("SELECT * FROM pogues where id=?");
			prepStmt.setString(1, id);
			rs = prepStmt.executeQuery();
			String result = "{}";
            int size = 0;
			while(rs.next()){
				result = rs.getString(2);
				if(++size>1){
					throw new NonUniqueResultException("Ambiguous ID");
				}
			}
			return (JSONObject)jsonParser.parse(result);
		} catch (SQLException e) {
			logger.error("SQLException");
			throw e;
		} catch(ParseException e){
			logger.error("Parser Exception");
			throw e;
		}
	}

	/**
	 * A method to delete the questionnaire with an id
	 * 
	 * @param id
	 *            id of the questionnaire
	 */
	public void deleteQuestionnaireByID(String id) throws Exception {
		try {
			prepStmt = connection.prepareStatement("DELETE FROM pogues where id=?");
			prepStmt.setString(1, id);
			prepStmt.execute();
		} catch (SQLException e) {
            logger.error("SQLException");
            throw e;
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
    public Map<String, JSONObject> getQuestionnairesByOwner(String owner) throws Exception {
        throw new Exception("Not implemented");
    }

	/**
	 * A method to create or replace a `Questionnaire` object.
	 * 
	 * @param id
	 *            id of the questionnaire, the JSON description of the
	 *            questionnaire
	 */
	/* TODO do we still need this one ?*/
	public void createOrReplaceQuestionnaire(String id, JSONObject questionnaire) throws Exception {
		logger.debug("Try to insert or update Questionnaire :  " + id);
		PGobject dataObject = new PGobject();
		dataObject.setType("json");
		try {
			dataObject.setValue(questionnaire.toJSONString());
			prepStmt = connection.prepareStatement("SELECT * FROM pogues where id=?");
			prepStmt.setString(1, id);
			rs = prepStmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
			}
			if (i == 1) {
				logger.debug("Try to update the questionnaire" + id);
				prepStmt = connection.prepareStatement("update pogues set data=? where id=?");
				prepStmt.setObject(1, dataObject);
				prepStmt.setString(2, id);
				prepStmt.executeUpdate();
				logger.info("Questionnaire updated");
			} else if (i == 0) {
				logger.debug("Try to insert the questionnaire" + id);
				prepStmt = connection.prepareStatement("insert into pogues (id, data) values (?, ?)");
				prepStmt.setString(1, id);
				prepStmt.setObject(2, dataObject);
				prepStmt.executeUpdate();
				logger.info("Questionnaire" + id + " created");
			}
		} catch (SQLException e) {
			logger.error("SQLException");
			e.printStackTrace();
			throw e;
		}

	}

	public void createQuestionnaire(JSONObject questionnaire) throws Exception {
	    String id  = (String)questionnaire.get("id");
        try {
            if(!this.getQuestionnaireByID(id).isEmpty()){
                throw new NonUniqueResultException("Entity already exists");
            }
            PGobject dataObject = new PGobject();
            dataObject.setType("json");
            dataObject.setValue(questionnaire.toJSONString());
            prepStmt = connection.prepareStatement("insert into pogues (id, data) values (?, ?)");
            prepStmt.setString(1, id);
            prepStmt.setObject(2, dataObject);
            prepStmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
    }

    public void updateQuestionnaire(JSONObject questionnaire) throws Exception {
        String id  = (String)questionnaire.get("id");
        try {
            if(this.getQuestionnaireByID(id).isEmpty()){
                throw new EntityNotFoundException("Not found");
            }
            PGobject dataObject = new PGobject();
            dataObject.setType("json");
            dataObject.setValue(questionnaire.toJSONString());
            logger.debug("Try to update the questionnaire" + id);
            prepStmt = connection.prepareStatement("update pogues set data=? where id=?");
            prepStmt.setObject(1, dataObject);
            prepStmt.setString(2, id);
            prepStmt.executeUpdate();
            logger.info("Questionnaire updated");
        } catch (SQLException e){
            logger.error("SQL Exception");
            throw e;
        } catch(Exception e) {
            throw e;
        }
    }

}
