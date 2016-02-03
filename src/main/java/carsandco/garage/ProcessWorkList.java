package carsandco.garage;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;


public class ProcessWorkList implements JavaDelegate {
	
	private static final Logger LOGGER = Logger.getLogger(ProcessWorkList.class);

	public void execute(DelegateExecution execution) throws Exception {
		String workList = (String) execution.getVariable("workList");

	    LOGGER.info("New work list registered: \n" + workList);
	}
		 
}