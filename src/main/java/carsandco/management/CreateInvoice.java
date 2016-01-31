package carsandco.management;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import com.google.gson.Gson;

import carsandco.tools.MongoClass;
import carsandco.tools.WorkList;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class CreateInvoice implements JavaDelegate {

	protected String IBAN = "DE12500105170648489890";
	protected String BIC = "PBNKDEFF";

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		RepairContract contract = (RepairContract) execution.getVariable("contract");
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
			execution.setVariable("invoice", invoice);
//Save invoice to database
			String invoiceJson = new Gson().toJson(invoice, InvoiceRequest.class);
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