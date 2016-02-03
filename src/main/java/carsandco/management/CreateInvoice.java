package carsandco.management;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import carsandco.tools.WorkList;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class CreateInvoice implements JavaDelegate {

	private static final Logger LOGGER = Logger.getLogger(CreateInvoice.class);
	protected final String IBAN = "DE12500105170648489890";
	protected final String BIC = "PBNKDEFF";

	public void execute(DelegateExecution execution) throws Exception {

		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		String worklist = (String) execution.getVariable("workList");
		WorkList list = JsonHandler.toObject(worklist, WorkList.class);
		try {
			LOGGER.info("Creating invoice...");
			// Create InvoiceRequest and fill data
			InvoiceRequest invoice = new InvoiceRequest(contract.getTransactionKey());
			invoice.setBic(BIC);
			invoice.setIban(IBAN);

			invoice.setAmount(list.getTotal());
			invoice.setDetailedRepairInformation(list.toString());
			invoice.setPurpose((String) execution.getVariable("contractID"));
			// Get debtor name from customerID with database query
			JSONObject debtor = MongoClass.getJSON("customers", "customerID", contract.getCustomerID());
			invoice.setDebtor(debtor.getString("name"));
			// Set process variable
			String invoiceJson = JsonHandler.toJson(invoice);
			execution.setVariable("invoice", invoiceJson);
			// Save invoice to database
			InputStream invoiceInput = new ByteArrayInputStream(invoiceJson.getBytes("UTF-8"));
			String invoiceID = MongoClass.insertJSON("invoices", invoiceInput);
			execution.setVariable("invoiceID", invoiceID);

			LOGGER.info("Invoice successfully created and saved in database with ID: " + invoiceID + "\n"
					+ JsonHandler.printJSON(invoiceJson));
		} catch (Exception e) {
			LOGGER.error("Error creating a new invoice with transaction key " + contract.getTransactionKey()
					+ " and debtor " + contract.getCustomerID() + " failed.");
			e.printStackTrace();
		}
	}

}