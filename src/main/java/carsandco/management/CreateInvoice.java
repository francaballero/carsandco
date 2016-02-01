package carsandco.management;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import carsandco.tools.JsonHandler;
import carsandco.tools.MongoClass;
import carsandco.tools.WorkList;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class CreateInvoice implements JavaDelegate {

	protected String IBAN = "DE12500105170648489890";
	protected String BIC = "PBNKDEFF";

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		WorkList list = (WorkList) execution.getVariable("workList");
		try {
//Create InvoiceRequest and fill data
			InvoiceRequest invoice = new InvoiceRequest(contract.getTransactionKey());
			invoice.setBic(BIC);
			invoice.setIban(IBAN);
			invoice.setAmount(list.getTotal());
			invoice.setDetailedRepairInformation(list.toString());
			invoice.setPurpose((String)execution.getVariable("contractID"));
//Get debtor name from customerID with database query
			JSONObject debtor = MongoClass.getJSON("customers", "customerID", contract.getCustomerID());
			invoice.setDebtor(debtor.getString("name"));
//Set process variable
			String invoiceJson =JsonHandler.toJson(invoice);
			execution.setVariable("invoice", invoiceJson);
//Save invoice to database		
			InputStream invoiceInput = new ByteArrayInputStream(invoiceJson.getBytes("UTF-8"));
			String invoiceID = MongoClass.insertJSON("invoices", invoiceInput);
			execution.setVariable("invoiceID", invoiceID);
			 
			System.out.println("Invoice successfully created: \n" + invoiceJson );
		} catch (Exception e) {
			System.err.println("Error creating a new invoice with transaction key " + contract.getTransactionKey()
					+ " and debtor " + contract.getCustomerID());
			e.printStackTrace();
		}
	}

}