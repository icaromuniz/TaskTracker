package au.edu.envirotech.tasktracker;

import java.sql.SQLException;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import au.edu.envirotech.tasktracker.model.User;
import au.edu.envirotech.tasktracker.services.PersistenceService;

public class AuthenticationComposer extends BindComposer<Component> {

	private static final long serialVersionUID = -2537702490116215106L;
	
	private Textbox textboxEmail;
	private Textbox textboxPassword;
	private Textbox textboxConfirmation;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);

		// execute bindings
		ConventionWires.wireFellows(getBinder().getView().getSpaceOwner(), this);
	}

	public void registerUser() {
		
		if ((textboxEmail.getValue() != null || !textboxEmail.getValue().isEmpty()) &&
				textboxPassword.getValue() != null || !textboxPassword.getValue().isEmpty() ) {
			
			int userId = 0;

			if (!textboxPassword.getValue().equals(textboxConfirmation.getValue())) {
				throw new WrongValueException(textboxConfirmation, "Not matching with the password. Please, try again.");
			}
			
			try {
				userId = PersistenceService.registerUser(textboxEmail.getValue(), textboxPassword.getValue());
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (userId == 0) {
				Messagebox.show("Something went wrong during registration.", "Registration error", 1, Messagebox.ERROR);
			} else {
				
				EventListener<Event> eventListener = new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						Executions.sendRedirect("/index.zul");
					}
				};
				
				Messagebox.show("User successfully registered!", "Registration success.", 1, Messagebox.INFORMATION, eventListener);
			}
		}
	}
	
	public void doLogin() {
		
		if ((textboxEmail.getValue() != null || !textboxEmail.getValue().isEmpty()) &&
				textboxPassword.getValue() != null || !textboxPassword.getValue().isEmpty() ) {
			
			User authorizedUser = null;
			
			try {
				authorizedUser = PersistenceService.getAuthorizedUser(textboxEmail.getValue(), textboxPassword.getValue());
			} catch (WrongValueException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (authorizedUser != null) {
				
				Sessions.getCurrent().setAttribute("auth_usr", authorizedUser);
				Executions.sendRedirect("/");
			
			} else {
				Messagebox.show("Authentication information doesn't match with registry.", "Login failed", 1, Messagebox.EXCLAMATION);
			}
		}
	}
}
