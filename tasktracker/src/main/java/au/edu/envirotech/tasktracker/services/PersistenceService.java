package au.edu.envirotech.tasktracker.services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import au.edu.envirotech.tasktracker.model.Task;
import au.edu.envirotech.tasktracker.model.User;

public class PersistenceService {
	
	private static Connection connection = null;
	
	private static Connection getConnection() throws SQLException {
		
		if (connection == null || connection.isClosed()) {
			
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/envirotech",
					"envirotech", "Burleigh7u8i9o0p");
		}
		
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

	public static void persistTaskList(List<Task> taskList) throws SQLException {

		String stringUpdate = "update task set date = ?, department = ?, description = ?, start_time = ?, finish_time = ?, "
				+ "under_plan = ?, note = ?, outcome = ?, follow_up_action = ? where id = ?;";
		
		String stringInsert = "insert into public.task (\"id\", \"user_id\", \"date\", \"department\", \"description\", \"under_plan\", "
				+ "\"start_time\", \"finish_time\", note) values (nextval('task_seq'), ?, ?, ?, ?, ?, ?, ?, ?);";

		String stringDelete = "delete from public.task where user_id = ? and id not in (";

		PreparedStatement preparedStatementInsert = null;
		PreparedStatement preparedStatementUpdate = null;
		PreparedStatement preparedStatementDelete = null;


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
					preparedStatementInsert.setString(3, task.getDepartment());
					preparedStatementInsert.setString(4, task.getDescription());
					preparedStatementInsert.setBoolean(5, task.isUnderPlan());
					preparedStatementInsert.setTime(6, new Time(task.getStartTime().getTime()));
					preparedStatementInsert.setTime(7, new Time(task.getFinishTime().getTime()));
					preparedStatementInsert.setString(8, task.getNote());
					
					preparedStatementInsert.executeUpdate();

				} else { // updating the existing task

					preparedStatementUpdate.setDate(1, new Date(task.getDate().getTime()));
					preparedStatementUpdate.setString(2, task.getDepartment());
					preparedStatementUpdate.setString(3, task.getDescription());
					preparedStatementUpdate.setTime(4, new Time(task.getStartTime().getTime()));
					preparedStatementUpdate.setTime(5, new Time(task.getFinishTime().getTime()));
					preparedStatementUpdate.setBoolean(6, task.isUnderPlan());
					preparedStatementUpdate.setString(7, task.getNote());
					preparedStatementUpdate.setString(8, task.getOutcome());
					preparedStatementUpdate.setString(9, task.getFollowUpAction());

					preparedStatementUpdate.setInt(10, task.getId());
					preparedStatementUpdate.executeUpdate();

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
	}

	public static List<Task> findTaskListByFilter(User user, java.util.Date date, String department, String description,
			java.util.Date startTime, java.util.Date finishTime, Boolean underPlan) throws SQLException {
		
		String stringSql = "select t.*, u.email from task t join \"user\" u on u.id = t.user_id where 1=1"; 
		ResultSet resultSet = null;
		
		// user
		if (user != null) {
			 stringSql += " and t.user_id = " + user.getId();
		}
		
		// date
		if (date != null) {
			stringSql += " and date = \'" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "\'";
		}
		
		// dept
		if (department != null && !department.isEmpty()) {
			stringSql += " and t.department like '" + department + "'";
		}
		
		// description
		if (description != null && !description.isEmpty()) {
			
			String[] keywordArray = description.split(" ");
			// where str like any (values('AAA%'), ('BBB%'), ('CCC%'));
			
			stringSql += " and lower(t.description) like any (values";
			
			for (String word : keywordArray) {
				stringSql += "('%" + word.toLowerCase() + "%'),";
			}
			
			stringSql = stringSql.substring(0, stringSql.length() - 1) + ")";
		}
		
		// planned or not
		if (underPlan != null) {
			stringSql += " and under_plan = " + underPlan;
		}
		
		// start time
		if (startTime != null) {
			stringSql += " and start_time >= " + startTime.getTime();
		}
		
		// finish time
		if (finishTime != null) {
			stringSql += " and finish_time <= " + finishTime.getTime();
		}
		
		stringSql += " order by email, t.date, t.start_time ";	
		
		resultSet = getConnection().prepareStatement(stringSql).executeQuery();
		
		if (resultSet != null) {
			
			ArrayList<Task> taskList = new ArrayList<Task>();
			
			while(resultSet.next()) {
				
				Task t = new Task();
				
				t.setUser(user != null ? user : new User(resultSet.getInt("user_id"), resultSet.getString("email")));
				t.setId(resultSet.getInt("id"));
				t.setDate(new Date(resultSet.getDate("date").getTime()));
				t.setDepartment(resultSet.getString("department"));
				t.setDescription(resultSet.getString("description"));
				t.setUnderPlan(resultSet.getBoolean("under_plan"));
				t.setStartTime(resultSet.getTime("start_time"));
				t.setFinishTime(resultSet.getTime("finish_time"));
				t.setNote(resultSet.getString("note"));
				t.setOutcome(resultSet.getString("outcome"));
				t.setFollowUpAction(resultSet.getString("follow_up_action"));
				
				taskList.add(t);
			}
			
			return taskList;
		}
		
		return null;
	}

	public static List<User> findUserById(Integer... idArray) throws SQLException {
		
		List<User> userList = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String stringSql = "select * from public.user";
		
		if (idArray.length > 0) {
			
			stringSql += " where id in (";
			
			for (Integer integer : idArray) {
				stringSql += integer + ",";
			}
			
			stringSql = stringSql.substring(0, stringSql.length() - 1);
			stringSql += ")";
		}
		
		try {

			preparedStatement = getConnection().prepareStatement(stringSql);
			resultSet = preparedStatement.executeQuery();
			
			if (resultSet != null) {
				
				userList = new ArrayList<User>();
				
				while (resultSet.next()) {
					userList.add(new User(resultSet.getInt("id"), resultSet.getString("email")));
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			preparedStatement.close();
			resultSet.close();
		}
		
		return userList;
	}
	
	public static String[] findDepartmentByFilter(String name) {
		
		String[] departmentArray = {"Executive", "Management", "Sales", "Marketing", "Admin / Enrolments", "Academic", "RTO Compliance",
				"Finance", "HR", "ICT", "Quality Assurance", "Special Projects", "Other"};
		
		return departmentArray;
	}

	public static List<String[]> findBarChartData(java.util.Date initialDate, java.util.Date finalDate, User user, String department) throws SQLException {
		
		List<String[]> dataList = new ArrayList<String[]>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String sqlQuery =
				"select " + 
				"	u.email, " + 
				"	t.department, " + 
				"	extract(hour from sum(t.finish_time - t.start_time))::integer as hour_spent " + 
				"from task t " +
				"join public.user u on u.id = t.user_id " +
				"where true "; 
		
		// filtering by initial date
		if (initialDate != null) {
			sqlQuery += " and t.date >= '" + new SimpleDateFormat("yyyy-MM-dd").format(initialDate) + "' ";
		}
		
		// filtering by final date
		if (finalDate != null) {
			sqlQuery += " and t.date <= '" + new SimpleDateFormat("yyyy-MM-dd").format(finalDate) + "' ";
		}
		
		// filtering by user
		if (user != null) {
			sqlQuery += " and u.email like '" + user.getEmail() + "' ";
		}
		
		// filtering by department
		if (department != null && !department.isEmpty()) {
			sqlQuery += " and t.department like '" + department + "' ";
		}
		
		sqlQuery  += 
				"group by u.email, t.department " + 
				"having extract(hour from sum(t.finish_time - t.start_time)) is not null " + 
				"order by u.email, t.department";
		
		try {
			
			preparedStatement = PersistenceService.getConnection().prepareStatement(sqlQuery);
			resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				String[] dataRow = {resultSet.getString("email"), resultSet.getString("department"), resultSet.getString("hour_spent")};
				dataList.add(dataRow);
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
			return null;
			
		} finally {
			
			resultSet.close();
			preparedStatement.close();
		}

		return dataList;
	}
}
