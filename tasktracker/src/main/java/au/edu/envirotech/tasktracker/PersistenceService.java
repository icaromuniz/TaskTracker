package au.edu.envirotech.tasktracker;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import au.edu.envirotech.tasktracker.model.User;

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
		String sql = "insert into public.user (id, email, password) values (nextval('user_seq'), ?, ?);"; // TODO Change database's column 'name' to 'email' 
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
	
	public static User getAuthorizedUser(String email, String password) throws SQLException {
		
		String sql = "select * from public.user where email like ? and password like ?";
		PreparedStatement preparedStatement = getConnection().prepareStatement(sql);
		ResultSet resultSet;
		
		preparedStatement.setString(1, email);
		preparedStatement.setString(2, password);
		
		resultSet = preparedStatement.executeQuery();
		
		if (resultSet != null && resultSet.next() && resultSet.getInt("id") > 0) {
			return new User(resultSet.getInt("id"), resultSet.getString("email"));
		}
		
		return null;
	}

	public static void saveTaskList(List<Task> taskList) throws SQLException {
		
		StringBuilder stringBuilder = new StringBuilder();
		PreparedStatement preparedStatement;
		
		/*
		 * for (Task task : taskList) { stringBuilder.
		 * append("update public.task set (id = nextval('task_seq'), user_id = ?, date = ?)"
		 * ); }
		 */
		
		preparedStatement = getConnection().prepareStatement("insert into public.task ( \"id\", \"user_id\", \"date\" ) "
				+ "values (nextval('task_seq'), ?, ?);");
		
		preparedStatement.setInt(1, taskList.get(0).getUser().getId());
		preparedStatement.setDate(2, new Date(taskList.get(0).getDate().getTime()));
		
		preparedStatement.executeUpdate();
	}

	public static List<Task> getTaskList(User user) throws SQLException {
		
		ResultSet resultSet = getConnection().prepareStatement("select * from task where user_id = "
				+ user.getId()).executeQuery();
		
		if (resultSet != null) {
			
			ArrayList<Task> taskList = new ArrayList<Task>();
			
			while(resultSet.next()) {
				
				Task t = new Task();
				
				t.setUser(user);
				t.setId(resultSet.getInt("id"));
				t.setDate(new Date(resultSet.getDate("date").getTime()));
				
				taskList.add(t);
			}
			
			return taskList;
		}
		
		return null;
	}
}
