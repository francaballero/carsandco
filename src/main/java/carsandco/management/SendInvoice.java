package carsandco.management;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import de.uniko.digicom.capitol.api.RestResponse;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.capitol.api.client.AccidentApiClient;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class SendInvoice implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
//Get current process variables
		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		String invoiceJson = (String) execution.getVariable("invoice");
		final InvoiceRequest invoice = JsonHandler.toObject(invoiceJson, InvoiceRequest.class);
		
//Compare customerIDs to find out who to send the invoice
		String customerID = contract.getCustomerID();		
		JSONObject capitol = MongoClass.getJSON("customers", "name", "Capitol");
		JSONObject bvis = MongoClass.getJSON("customers", "name", "BVIS");
		
		//Case Capitol
		if (customerID.equals(capitol.getString("customerID"))) {
			Runnable runSendInvoice = new Runnable(){
				public void run(){
					AccidentApiClient capitol_api = new AccidentApiClient();
					RestResponse response = capitol_api.continueAccident(invoice);
					if(response.hasSucceeded()){
						System.out.println("Sending invoice to Capitol successful!");
					} else {
						System.out.println(response.getMessage());
					}
				}
			};
			System.out.println("Sending Invoice to Capitol...");
			new Thread(runSendInvoice).start();
		}
		//Case BVIS
		if (customerID.equals(bvis.getString("customerID"))) {
			//TODO Update BVIS URL to send invoice
			Runnable runSendInvoice = new Runnable(){
				public void run(){
					AccidentApiClient capitol_api = new AccidentApiClient();
					RestResponse response = capitol_api.continueAccident(invoice);
					if(response.hasSucceeded()){
						System.out.println("Sending invoice to BVIS successful!");
					} else {
						System.out.println(response.getMessage());
					}
				}
			};
			System.out.println("Sending Invoice to BVIS...");
			new Thread(runSendInvoice).start();
		} else {
			System.err.println("Error sending invoice " + invoice.getPurpose() 
								+ " with Transaction Key " + invoice.getTransactionKey() 
								+ ". No URL/Debtor could be found.");
		}
	}

}
