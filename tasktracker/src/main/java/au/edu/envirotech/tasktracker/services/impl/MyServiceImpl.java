package au.edu.envirotech.tasktracker.services.impl;

import au.edu.envirotech.tasktracker.services.MyService;
import java.text.DateFormat;
import java.util.Date;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("myService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MyServiceImpl implements MyService {

	public String ask(String question) {
		return DateFormat.getDateInstance().format(new Date());
	}

}
