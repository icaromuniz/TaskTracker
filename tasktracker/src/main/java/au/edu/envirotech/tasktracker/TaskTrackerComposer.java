package au.edu.envirotech.tasktracker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;

import au.edu.envirotech.tasktracker.model.Task;
import au.edu.envirotech.tasktracker.model.User;
import au.edu.envirotech.tasktracker.services.PersistenceService;

public class TaskTrackerComposer extends BindComposer<Component> {

	private static final long serialVersionUID = -3477781842471158543L;

	@Wire
	private Listbox listbox;

	private List<Task> taskList;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		if (!isUserAuthorized()) {
			Executions.sendRedirect("/login.zul");
		} else {
			
			super.doAfterCompose(comp);
			
			// executa os binds
			ConventionWires.wireFellows(getBinder().getView().getSpaceOwner(), this);
			
			taskList = PersistenceService.findTaskListByFilter((User) Sessions.getCurrent().getAttribute("auth_usr"), 
					null, null, null, null);
			
			// initializes the list with one blank task
			if (taskList == null || taskList.isEmpty()) {
				
				taskList = new ArrayList<Task>();
				Task t = new Task();
				
				t.setUser((User) Sessions.getCurrent().getAttribute("auth_usr"));
				taskList.add(t);
			}
			
			getBinder().notifyChange(this, "*");
		}
	}

	private boolean isUserAuthorized() {

		if (Sessions.getCurrent().getAttribute("auth_usr") != null) {
			return true;
		}

		return false;
	}

	public String getCurrentUser() {

		Object user = Sessions.getCurrent().getAttribute("auth_usr");

		return user != null ? ((User) user).getEmail() : null;
	}
	
	public List<String> getDepartmentList(){
		return Arrays.asList(PersistenceService.findDepartmentByFilter(null));
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}

	public void addTask() {
		
		Task t = new Task();
		
		t.setUser((User) Sessions.getCurrent().getAttribute("auth_usr"));
		taskList.add(t);
		
		getBinder().notifyChange(this, "*");
		
		Clients.scrollIntoView(listbox.getLastChild());
	}

	public void removeTask() {

		Set<?> set = ((ListModelList<?>) listbox.getModel()).getSelection();
		taskList.removeAll(set);
		getBinder().notifyChange(this, "*");
	}

	public void saveTaskList() {
		
		if (taskList.isEmpty()) {
			Messagebox.show("You can't save a blank list.", "Persistence failed", 1, Messagebox.ERROR, null);
			return;
		}

		// TODO Validate data
		for (Listitem listitem : listbox.getItems()) {
//			listitem.get
		}

		try {
			PersistenceService.persistTaskList(taskList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Messagebox.show("Error saving the new tasks", "Persistence failed", 1, Messagebox.ERROR, null);
			return;
		}

		Messagebox.show("All your tasks are saved!", "Task Tracker - Success!", 1, Messagebox.INFORMATION, null);
	}
}
