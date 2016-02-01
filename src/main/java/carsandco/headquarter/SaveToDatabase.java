package carsandco.headquarter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import carsandco.tools.MongoClass;

public class SaveToDatabase implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {

//Save contract to database and add the contractID to the process variables
		String contractJson = (String) execution.getVariable("contract");
		InputStream contractInput = new ByteArrayInputStream(contractJson.getBytes("UTF-8"));
		String contractID = MongoClass.insertJSON("contracts", contractInput);
		execution.setVariable("contractID", contractID);
		
	}
	

}
