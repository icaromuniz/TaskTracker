package au.edu.envirotech.tasktracker;

import java.util.Date;

import au.edu.envirotech.tasktracker.model.User;

public class Task {

	private int id;
	private User user;
	private Date date;
	private String department;
	private String description;
	private Date start;
	private Date finish;
	private boolean plannedTask;
	private String note;
	private String outcome;
	private String followUpAction;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getFinish() {
		return finish;
	}

	public void setFinish(Date finish) {
		this.finish = finish;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public boolean isPlannedTask() {
		return plannedTask;
	}

	public void setPlannedTask(boolean plannedTask) {
		this.plannedTask = plannedTask;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getFollowUpAction() {
		return followUpAction;
	}

	public void setFollowUpAction(String followUpAction) {
		this.followUpAction = followUpAction;
	}
}
