package carsandco.management;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import de.uniko.digicom.bvis.api.client.BvisApiClient;
import de.uniko.digicom.capitol.api.RestResponse;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.capitol.api.client.AccidentApiClient;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class SendInvoice implements JavaDelegate {

	private static final Logger LOGGER = Logger.getLogger(SendInvoice.class);

	public void execute(DelegateExecution execution) throws Exception {
		// Get current process variables
		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		String invoiceJson = (String) execution.getVariable("invoice");
		final InvoiceRequest invoice = JsonHandler.toObject(invoiceJson, InvoiceRequest.class);
		String invoiceID = (String) execution.getVariable("invoiceID");
		// Compare customerIDs to find out who to send the invoice
		try {
			String customerID = contract.getCustomerID();
			JSONObject capitol = MongoClass.getJSON("customers", "name", "Capitol");
			JSONObject bvis = MongoClass.getJSON("customers", "name", "BVIS");

			// Case Capitol
			if (customerID.equals(capitol.getString("customerID"))) {
				Runnable runSendInvoice = new Runnable() {
					public void run() {

						RestResponse response = AccidentApiClient.continueAccident(invoice);
						if (response.hasSucceeded()) {
							LOGGER.info("Sending invoice to Capitol successful!");
						} else {
							LOGGER.error(response.getMessage());
						}
					}
				};
				LOGGER.info("Sending Invoice to Capitol...");
				new Thread(runSendInvoice).start();
			}
			// Case BVIS
			if (customerID.equals(bvis.getString("customerID"))) {
				Runnable runSendInvoice = new Runnable() {
					public void run() {
				
						RestResponse response = BvisApiClient.sendInvoiceRequest(invoice);
						if (response.hasSucceeded()) {
							LOGGER.info("Sending invoice to BVIS successful!");
						} else {
							LOGGER.error(response.getMessage());
						}
					}
				};
				LOGGER.info("Sending Invoice to BVIS...");
				new Thread(runSendInvoice).start();
			} else {
				LOGGER.error("Error sending invoice " + invoiceID + ". No URL/Debtor could be found.");
			}
		} catch (Exception e) {
			LOGGER.error("Error sending the invoice " + invoiceID);
		}
	}

}
