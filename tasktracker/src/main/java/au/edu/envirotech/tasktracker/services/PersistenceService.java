package au.edu.envirotech.tasktracker.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import au.edu.envirotech.tasktracker.model.Task;
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
		
		PreparedStatement preparedStatement = null;
		String sql = "insert into public.user (id, email, password) values (nextval('user_seq'), ?, ?);"; 
		int newId = 0;
		
		try {
			
			preparedStatement = getConnection().prepareStatement(sql);
			
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			newId = preparedStatement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			preparedStatement.close();
		}
		
		return newId;
	}
	
	public static User getAuthorizedUser(String email, String password) throws SQLException {
		
		String sql = "select * from public.user where email like ? and password like ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet;
		
		try {
			
			preparedStatement = getConnection().prepareStatement(sql);
			
			preparedStatement.setString(1, email);
			preparedStatement.setString(2, password);
			
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet != null && resultSet.next() && resultSet.getInt("id") > 0) {
				return new User(resultSet.getInt("id"), resultSet.getString("email"));
			}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			throw e;
			
		} finally {
			
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
		
		return null;
	}

	public static void saveTaskList(List<Task> taskList) throws SQLException {

		String stringDelete = "delete from public.task where user_id = ? and id not in ("; // TODO delete absent records
		String stringInsert = "insert into public.task (\"id\", \"user_id\", \"date\" ) values (nextval('task_seq'), ?, ?);";
		String stringUpdate = ""; // TODO insert new records

		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdate = null;
		PreparedStatement preparedStatementDelete = null;

		// *insert new tasks
		// *update tasks
		// *delete absent tasks

		try {

			preparedStatementInsert = getConnection().prepareStatement(stringInsert);
			preparedStatementUpdate = getConnection().prepareStatement(stringUpdate);

			// taking the present ids to be kept
			for (Task task : taskList) {
				stringDelete += task.getId() + ",";
			}
			
			// finishing the deletion string
			stringDelete = stringDelete.substring(0, stringDelete.length() - 1) + ");";	// cuts the last "," and closes the parenthesis

			// deleting records of tasks removed from the list
			preparedStatementDelete = getConnection().prepareStatement(stringDelete);
			preparedStatementDelete.setInt(1, taskList.get(0).getUser().getId());
			preparedStatementDelete.executeUpdate();

			for (Task task : taskList) {

				if (task.getId() == 0) { // inserting the new task

					preparedStatementInsert.setInt(1, task.getUser().getId());
					preparedStatementInsert.setDate(2, new Date(task.getDate().getTime()));
					preparedStatementInsert.executeUpdate();

				} else { // updating the existing task

				}
			}

		} catch (SQLException e) {

			getConnection().rollback();
			e.printStackTrace();
			throw e;

		} finally {

			if (preparedStatementInsert != null) preparedStatementInsert.close();
			if (preparedStatementUpdate != null) preparedStatementUpdate.close();
			if (preparedStatementDelete != null) preparedStatementDelete.close();
		}

		// FIXME execute removal of absent tasks from database
	}

	public static List<Task> findTaskListByUser(User user) throws SQLException {
		
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
	
	/*
	 * public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws
	 * SQLException {
	 * 
	 * Connection con = getConnection(); PreparedStatement updateSales = null;
	 * PreparedStatement updateTotal = null;
	 * 
	 * String updateString = "update " + dbName + ".COFFEES " +
	 * "set SALES = ? where COF_NAME = ?";
	 * 
	 * String updateStatement = "update " + dbName + ".COFFEES " +
	 * "set TOTAL = TOTAL + ? " + "where COF_NAME = ?";
	 * 
	 * try { con.setAutoCommit(false); updateSales =
	 * con.prepareStatement(updateString); updateTotal =
	 * con.prepareStatement(updateStatement);
	 * 
	 * for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
	 * 
	 * updateSales.setInt(1, e.getValue().intValue()); updateSales.setString(2,
	 * e.getKey()); updateSales.executeUpdate();
	 * 
	 * updateTotal.setInt(1, e.getValue().intValue()); updateTotal.setString(2,
	 * e.getKey()); updateTotal.executeUpdate();
	 * 
	 * con.commit(); } } catch (SQLException e ) {
	 * JDBCTutorialUtilities.printSQLException(e); if (con != null) { try {
	 * System.err.print("Transaction is being rolled back"); con.rollback(); }
	 * catch(SQLException excep) { JDBCTutorialUtilities.printSQLException(excep); }
	 * } } finally { if (updateSales != null) { updateSales.close(); } if
	 * (updateTotal != null) { updateTotal.close(); } con.setAutoCommit(true); } }
	 */
}
