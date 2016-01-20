package carsandco.garage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

import carsandco.tools.WorkList;

public class ProcessWorkList implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		WorkList workList = (WorkList) execution.getVariable("workList");

	    System.out.println("New work list registered:");
	    System.out.println(workList);
	}
		 
}
