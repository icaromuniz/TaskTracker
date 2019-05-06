package au.edu.envirotech.tasktracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PersistenceService {

	public static int registerUser(String email, String password) throws ClassNotFoundException {
		
		Class.forName("org.postgresql.Driver");
		Connection connection = null;
		String sql = "insert into public.user (id, name, password) values (nextval('user_seq'), ?, ?);";
		int id = 0;
		
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/envirotech",
					"envirotech", "Burleigh7u8i9o0p");
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			id = preparedStatement.executeUpdate();
			
			connection.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;
	}
}
