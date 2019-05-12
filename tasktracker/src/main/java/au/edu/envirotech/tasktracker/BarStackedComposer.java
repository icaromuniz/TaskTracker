package au.edu.envirotech.tasktracker;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Chart;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.Window;

import au.edu.envirotech.tasktracker.model.BarStackedData;


public class BarStackedComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 3072431763461659347L;
	
	@Wire
    Chart chart;
    
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);
        
        SimpleCategoryModel simpleCategoryModel = new SimpleCategoryModel();
        
        simpleCategoryModel.setValue("Priscila", "ICT", 9);
        simpleCategoryModel.setValue("Priscila", "Academic", 5);
        simpleCategoryModel.setValue("Priscila", "Marketing", 9);
//        
        simpleCategoryModel.setValue("Benjamin", "Marketing", 9);
//        simpleCategoryModel.setValue("Benjamin", "ICT", 5);
//        simpleCategoryModel.setValue("Rebeca", "Academic", 5);
        
        simpleCategoryModel.setValue("Fatima", "Marketing", 9);
        
        chart.setModel(BarStackedData.getCategoryModel());
        
        ((Chart)chart.getFellow("chartNew")).setModel(BarStackedData.getCategoryModel());
        ((Chart)chart.getFellow("chartPie")).setModel(simpleCategoryModel);
        
//        chart.getYAxis().setMin(0);
//        chart.getYAxis().setTitle("Total fruit consumption");
//        
//        chart.getLegend().setReversed(true);
//        
//        chart.getPlotOptions().getSeries().setStacking("normal");
//        
//        chart.getCredits().setEnabled(false);
    }
}
