package au.edu.envirotech.tasktracker.services;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;

public class ComboitemYesNoRenderer implements ComboitemRenderer<Boolean> {

	public void render(Comboitem item, Boolean data, int index) throws Exception {
        item.setLabel(data ? "Yes" : "No");
        item.setDescription(data ? "True" : "False");
	}
}
