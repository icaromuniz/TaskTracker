package au.edu.envirotech.tasktracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersistenceService {
	
	private static Connection getConnection() throws SQLException {
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/envirotech",
				"envirotech", "Burleigh7u8i9o0p");
		
		return connection;
	}

	public static int registerUser(String email, String password) throws SQLException {
		
		Connection connection = getConnection();
		String sql = "insert into public.user (id, name, password) values (nextval('user_seq'), ?, ?);"; // TODO Change database's column 'name' to 'email' 
		int newId = 0;
		
		try {
			
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			newId = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newId;
	}
	
	public static boolean isUserAuthorized(String email, String password) throws SQLException {
		
		String sql = "select * from public.user where name like ?";
		PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
		ResultSet resultSet;
		
		preparedStatement.setString(1, email);
//		preparedStatement.setString(2, password);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet != null && resultSet.next() && resultSet.getInt("id") > 0) {
			return true;
		}
		
		return false;
	}
}
