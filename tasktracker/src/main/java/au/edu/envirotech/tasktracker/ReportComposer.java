package au.edu.envirotech.tasktracker;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import au.edu.envirotech.tasktracker.model.Task;
import au.edu.envirotech.tasktracker.model.User;
import au.edu.envirotech.tasktracker.services.PersistenceService;

public class ReportComposer extends BindComposer<Component> {

	private static final long serialVersionUID = -3477781842471158543L;

	@Wire
	private Listbox listbox;
	
	@Wire
	private Combobox comboboxUser;

	private List<Task> taskList;

	@Override
	public void doAfterCompose(Component comp) throws Exception {

		if (!isUserAuthorized()) {
			Executions.sendRedirect("/login.zul");
		} else {
			
			User currentUser = (User) Sessions.getCurrent().getAttribute("auth_usr");
			
			super.doAfterCompose(comp);
			
			// executa os binds
			ConventionWires.wireFellows(getBinder().getView().getSpaceOwner(), this);

			// load user's set of tasks
			this.filterTaskList(currentUser);
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
	
	public void filterTaskList(User currentUser) {

		Comboitem comboitemUnderPlan = ((Combobox) comboboxUser.getFellow("comboboxUnderPlan")).getSelectedItem();
		Comboitem comboitemDepartment = ((Combobox) comboboxUser.getFellow("comboboxDepartment")).getSelectedItem();

		User userFilter = comboboxUser.getSelectedItem() != null ? (User) comboboxUser.getSelectedItem().getValue() : null;
		Date dateFilter = ((Datebox) comboboxUser.getFellow("dateboxDate")).getValue();
		String departmentFilter = comboitemDepartment != null ? (String) comboitemDepartment.getValue() : null;
		Boolean underPlan = comboitemUnderPlan != null ? Boolean.parseBoolean((String) comboitemUnderPlan.getValue()) : null;
		String description = ((Textbox) comboboxUser.getFellow("textboxDescription")).getValue();
		Date startTime = null; // FIXME
		Date finishTime = null; // FIXME

		try {
			taskList = PersistenceService.findTaskListByFilter(currentUser != null ? currentUser : userFilter,
					dateFilter, departmentFilter, description, startTime, finishTime, underPlan);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		getBinder().notifyChange(this, "*");
	}

	public List<Task> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<Task> taskList) {
		this.taskList = taskList;
	}
	
	public List<User> getUserList() {
		try {
			return PersistenceService.findUserById(new Integer[0]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String[] getDepartmentList() {
		return PersistenceService.findDepartmentByFilter(null);
	}
}
