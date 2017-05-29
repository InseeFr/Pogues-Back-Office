package fr.insee.pogues.persistence.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.postgresql.util.PGobject;

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

	
	/**
	 * Contructor for Questionnaire Service Query Postrgesql implementation, init the connection.
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
		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.error("SQLException - Impossible to close the connection");
			e.printStackTrace();
		}

	}

	
	/**
	 * A method to get the `QuestionnaireList` object in the database
	 * 
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public Map<String, String> getQuestionnaires() {

		Map<String, String> questionnaires = new HashMap<String, String>();
		try {
			rs = stmt.executeQuery("SELECT * FROM pogues");
			while (rs.next()) {
				questionnaires.put(rs.getString(1), rs.getString(2));
			}
		} catch (SQLException e) {
			logger.error("SQLException");
			e.printStackTrace();
		}
		return questionnaires;

	}

	/**
	 * A method to get the questionnaire with an id
	 * 
	 * @param the id of the questionnaire
	 * @return the JSON description of the questionnaire
	 */
	public String getQuestionnaireByID(String id) {
		String questionnaire = "";
		try {
			prepStmt = connection.prepareStatement("SELECT * FROM pogues where id=?");
			prepStmt.setString(1, id);
			rs = prepStmt.executeQuery();
			// TODO verify that the resultSet length < 2
			while (rs.next()) {
				questionnaire = rs.getString(2);
			}
		} catch (SQLException e) {
			logger.error("SQLException");
			e.printStackTrace();
		}
		return questionnaire;
	}

	/**
	 * A method to get the `QuestionnaireList` with an owner in the database
	 * 
	 * @param the owner of the questionnaire
	 * @return the questionnaires list Map<String,String>, key : the id of the
	 *         questionnaire, value : the JSON description of the questionnaire
	 */
	public Map<String, String> getQuestionnairesByOwner(String owner) {
		// TODO
		return null;
	}
	
	/**
	 * A method to create or replace a `Questionnaire` object.
	 * 
	 * @param the id of the questionnaire, the JSON description of the questionnaire
	 */
	public void createOrReplaceQuestionnaire(String id, String questionnaire) {
		logger.debug("Try to insert or update Questionnaire :  " + id);
		PGobject dataObject = new PGobject();
		dataObject.setType("json");
		try {
			dataObject.setValue(questionnaire);
			prepStmt = connection.prepareStatement("SELECT * FROM pogues where id=?");
			prepStmt.setString(1, id);
			rs = prepStmt.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
			}
			if (i == 1) {
				logger.debug("Try to update the questionnaire");
				prepStmt = connection.prepareStatement("update pogues set data=? where id=?");
				prepStmt.setObject(1, dataObject);
				prepStmt.setString(2, id);
				prepStmt.executeUpdate();
				prepStmt.close();
				logger.info("Questionnaire updated");
			} else if (i == 0) {
				logger.debug("Try to insert the questionnaire");
				prepStmt = connection.prepareStatement("insert into pogues (id, data) values (?, ?)");
				prepStmt.setString(1, id);
				prepStmt.setObject(2, dataObject);
				prepStmt.executeUpdate();
				prepStmt.close();
				logger.info("Questionnaire created");
			}
		} catch (SQLException e) {
			logger.error("SQLException");
			e.printStackTrace();
		}
		
		

	}

}
