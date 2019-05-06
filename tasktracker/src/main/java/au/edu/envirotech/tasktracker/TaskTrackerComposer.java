package au.edu.envirotech.tasktracker;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.ConventionWires;

public class TaskTrackerComposer extends BindComposer<Component> {

	private static final long serialVersionUID = -3477781842471158543L;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {

		super.doAfterCompose(comp);

		if (!isUserAuthorized()) {
			Executions.sendRedirect("/login.zul");
		}

		// executa os binds
		ConventionWires.wireFellows(getBinder().getView().getSpaceOwner(), this);

	}

	private boolean isUserAuthorized() {
		
		if(Sessions.getCurrent().getAttribute("auth_usr") != null) {
			return true;
		}
		
		return false;
	}

	public String getUser() {
		return "icaro";
	}
}
