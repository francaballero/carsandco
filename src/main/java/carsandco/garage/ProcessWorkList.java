package carsandco.garage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

public class ProcessWorkList implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		WorkList workList = (WorkList) execution.getVariable("workList");

	    System.out.println("New work list registered:");
	    System.out.println(workList);
	    
		//String contract = (String) execution.getVariable("contract");
	    //JSONObject json = (JSONObject) new JSONParser().parse(contract);

	    //CALCULATE JSON ARRAY FROM LIST

	    //json.put("List of work", new JSON(LIST OF WORK JSON ARRAY))	
	}
		 
}
