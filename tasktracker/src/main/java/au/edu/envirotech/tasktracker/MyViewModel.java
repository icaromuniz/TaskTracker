package au.edu.envirotech.tasktracker;

import au.edu.envirotech.tasktracker.services.MyService;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class MyViewModel {

	@WireVariable
	private MyService myService;
	private String answer;

	@Init
	public void init() {
		answer = "?";
	}

	@Command
	@NotifyChange("answer")
	public void cmd() {
		answer = myService.ask("What day is today?");
	}

	public String getAnswer() {
		return answer;
	}
}
