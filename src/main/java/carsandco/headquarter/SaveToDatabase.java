package carsandco.headquarter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SaveToDatabase implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		//TODO Save contract to contracts-collection (DB)
		//TODO Extract transaction key and customerID from contract (RepairContract.java class)
		//TODO Save transaction key, customerID, contractID and current processID into the tansactions-collection (DB)

		
	}
	

}
