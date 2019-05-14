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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timebox;

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
					null, null, null, null, null, null);
			
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
		
		getBinder().notifyChange(this, "taskList");
		
//		Clients.scrollIntoView(listbox.getLastChild().getPreviousSibling()); // FIXME
	}

	public void removeTask() {

		Set<?> set = ((ListModelList<?>) listbox.getModel()).getSelection();
		taskList.removeAll(set);
		getBinder().notifyChange(this, "taskList");
	}

	public void saveTaskList() {

		List<WrongValueException> validatioExceptionList = new ArrayList<WrongValueException>();

		if (taskList.isEmpty()) {
			Messagebox.show("You can't save a blank list.", "Persistence failed", 1, Messagebox.ERROR, null);
			return;
		}

		// validation
		for (Listitem listitem : listbox.getItems()) {

			Datebox datebox = (Datebox) listitem.getFirstChild().getNextSibling().getFirstChild();
			Combobox comboboxDepartment = (Combobox) datebox.getParent().getNextSibling().getFirstChild();
			Textbox textboxDescription = (Textbox) comboboxDepartment.getParent().getNextSibling().getFirstChild();
			Timebox timeboxStart = (Timebox) textboxDescription.getParent().getNextSibling().getFirstChild();
			Timebox timeboxFinish = (Timebox) timeboxStart.getParent().getNextSibling().getFirstChild();
			
			if (datebox.getValue() == null) {
				validatioExceptionList.add(new WrongValueException(datebox, "Mandatory field"));
			}
			
			if (comboboxDepartment.getValue().isEmpty()) {
				validatioExceptionList.add(new WrongValueException(comboboxDepartment, "Mandatory field"));
			}
			
			if (textboxDescription.getValue().isEmpty()) {
				validatioExceptionList.add(new WrongValueException(textboxDescription, "Mandatory field"));
			}
			
			if (timeboxStart.getValue() == null) {
				validatioExceptionList.add(new WrongValueException(timeboxStart, "Mandatory field"));
			}
			
			if (timeboxFinish.getValue() == null) {
				validatioExceptionList.add(new WrongValueException(timeboxFinish, "Mandatory field"));
			}
		}

		// throw exceptions to the view
		if (!validatioExceptionList.isEmpty()) {
			throw new WrongValuesException(validatioExceptionList.toArray(new WrongValueException[0]));
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
