package carsandco.headquarter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.google.gson.Gson;

import carsandco.tools.MongoClass;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class SaveToDatabase implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {

//Save contract to database and add the contractID to the process variables
		RepairContract contract = (RepairContract) execution.getVariable("contract");
		InputStream contractInput = new ByteArrayInputStream(new Gson().toJson(contract, RepairContract.class).getBytes("UTF-8"));
		String contractID = MongoClass.insertJSON("invoices", contractInput);
		execution.setVariable("contractID", contractID);
		
	}
	

}
