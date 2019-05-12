package au.edu.envirotech.tasktracker.model;

import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.SimpleCategoryModel;

public class BarStackedData {
    private static CategoryModel model;
    static {
        model = new SimpleCategoryModel();
//        model.setValue("John", "Apples", new Integer(5));
//        model.setValue("John", "Oranges", new Integer(3));
//        model.setValue("John", "Pears", new Integer(4));
//        model.setValue("John", "Grapes", new Integer(7));
//        model.setValue("John", "Bananas", new Integer(2));
//        model.setValue("Jane", "Apples", new Integer(2));
//        model.setValue("Jane", "Oranges", new Integer(2));
//        model.setValue("Jane", "Pears", new Integer(3));
//        model.setValue("Jane", "Grapes", new Integer(2));
//        model.setValue("Jane", "Bananas", new Integer(1));
//        model.setValue("Joe", "Apples", new Integer(3));
//        model.setValue("Joe", "Oranges", new Integer(4));
//        model.setValue("Joe", "Pears", new Integer(4));
//        model.setValue("Joe", "Grapes", new Integer(2));
//        model.setValue("Joe", "Bananas", new Integer(5));
        
        model.setValue("Icaro", "Executive", 10);
        model.setValue("Icaro", "Management", 3);
        model.setValue("Icaro", "Sales", 5);
        model.setValue("Icaro", "Marketing", 10);
        model.setValue("Icaro", "Admin / Enrolments", 3);
        model.setValue("Icaro", "Academic", 5);
        model.setValue("Icaro", "RTO Compliance", 10);
        model.setValue("Icaro", "Finance", 3);
        model.setValue("Icaro", "HR", 5);
        model.setValue("Icaro", "Quality Assurance", 10);
        model.setValue("Icaro", "ICT", 3);
        model.setValue("Icaro", "Special Projects", 5);
        
        model.setValue("Priscila", "ICT", 9);
        model.setValue("Priscila", "Academic", 5);
        model.setValue("Priscila", "Marketing", 9);
        
        model.setValue("Benjamin", "Marketing", 9);
        model.setValue("Benjamin", "ICT", 5);
        model.setValue("Rebeca", "Academic", 5);
        
        model.setValue("Fatima", "Marketing", 9);
        model.setValue("Fatima", "ICT", 5);
        model.setValue("Rebeca", "Academic", 5);

        model.setValue("Rebeca", "Marketing", 5);
        model.setValue("Rebeca", "ICT", 5);
        model.setValue("Rebeca", "Academic", 5);
        
        model.setValue("Susan", "Executive", 10);
        model.setValue("Susan", "Management", 3);
        model.setValue("Susan", "Sales", 5);
        model.setValue("Susan", "Marketing", 10);
        model.setValue("Susan", "Admin / Enrolments", 3);
        model.setValue("Susan", "Academic", 5);
        model.setValue("Susan", "RTO Compliance", 10);
        model.setValue("Susan", "Finance", 3);
        model.setValue("Susan", "HR", 5);
        model.setValue("Susan", "Quality Assurance", 10);
        model.setValue("Susan", "ICT", 3);
        model.setValue("Susan", "Special Projects", 5);
        
        model.setValue("Noah", "Executive", 10);
        model.setValue("Noah", "Management", 3);
        model.setValue("Noah", "Sales", 5);
        model.setValue("Susan", "Marketing", 10);
        model.setValue("Noah", "Admin / Enrolments", 3);
        model.setValue("Noah", "Academic", 5);
        model.setValue("Noah", "RTO Compliance", 10);
        model.setValue("Noah", "Finance", 3);
        model.setValue("Noah", "HR", 5);
        model.setValue("Noah", "Quality Assurance", 10);
        model.setValue("Noah", "ICT", 3);
        model.setValue("Noah", "Special Projects", 5);
        
//        Executive
//        Management
//        Sales
//        Marketing
//        Admin / Enrolments
//        Academic
//        RTO Compliance
//        Finance
//        HR
//        ICT
//        Quality Assurance
//        Special Projects
//        Other
    }
    
    public static CategoryModel getCategoryModel() {
        return model;
    }
}