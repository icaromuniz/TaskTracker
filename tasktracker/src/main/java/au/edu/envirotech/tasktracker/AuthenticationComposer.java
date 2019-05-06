package au.edu.envirotech.tasktracker;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

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
			
			if (!textboxPassword.getValue().equals(textboxConfirmation.getValue())) {
				throw new WrongValueException(textboxConfirmation, "Not matching with the password. Please, try again.");
			}
			
			int userId = 0;
			
			try {
				userId = PersistenceService.registerUser(textboxEmail.getValue(), textboxPassword.getValue());
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (userId == 0) {
				Messagebox.show("Something went wrong", "Registration error", 1, null);
			} else {
				
				EventListener<Event> eventListener = new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						Executions.sendRedirect("/index.zul");
					}
				};
				
				Messagebox.show("User successfully registered", "Registration success", 1, Messagebox.INFORMATION, eventListener);
			}
		}
	}
}
