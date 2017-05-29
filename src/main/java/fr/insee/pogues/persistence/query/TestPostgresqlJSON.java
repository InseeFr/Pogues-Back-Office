package fr.insee.pogues.persistence.query;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.util.PGobject;

public class TestPostgresqlJSON {

	public static void main(String[] argv) throws SQLException {

		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
			return;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://dvrmspogfoldb01.ad.insee.intra:1983/di_pg_rmspogfo_dv01", "user_rmspogfo_loc",
					"rmeS6789");

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}

	
		String id="fr.insee-POPO-QPO-DOC";
		System.out.println("Try to select this JSON data now : ");
		
		try {
			
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM pogues where id=?");
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			while (rs.next())
			{
			   System.out.print("Column 1 returned ");
			   System.out.println(rs.getString(1));
			   System.out.print("Column 2 returned ");
			   System.out.println(rs.getString(2));
			} 
			rs.close();
			stmt.close();
		} catch (SQLException sqle) {
			System.err.println("Something exploded running the insert: " + sqle.getMessage());
		}
		
		System.out.println("Select statement success");
		
	}

}