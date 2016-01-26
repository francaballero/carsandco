package carsandco.management;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import carsandco.tools.MongoClass;
import carsandco.tools.WorkList;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class CreateBill implements JavaDelegate {

	protected String IBAN = "DE12500105170648489890";
	protected String BIC = "PBNKDEFF";

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		RepairContract contract = (RepairContract) execution.getVariable("contract");
		WorkList list = (WorkList) execution.getVariable("workList");
		try {
			InvoiceRequest invoice = new InvoiceRequest(contract.getTransactionKey());
			invoice.setBic(BIC);
			invoice.setIban(IBAN);
			invoice.setAmount(list.getTotal());
			invoice.setDetailedRepairInformation(list.toString());

			JSONObject transaction = new JSONObject(
					MongoClass.getJSON("transactions", "transactionkey", contract.getTransactionKey()));
			invoice.setPurpose(transaction.getString("contractID"));

			JSONObject debtor = new JSONObject(
					MongoClass.getJSON("customers", "id", Integer.toString(contract.getCustomerID())));
			invoice.setDebtor(debtor.getString("name"));

			execution.setVariable("invoice", invoice);

			// TODO Save invoice in database (invoice-collection or add to contract?)

			System.out.println(
					"Invoice over " + list.getTotal() + "Euro with transaction key " + contract.getTransactionKey()
							+ " and debtor " + debtor.getString("name") + " successfully created.");
		} catch (Exception e) {
			System.out.println("Error creating a new invoice with transaction key " + contract.getTransactionKey()
					+ " and debtor " + contract.getCustomerID());
			e.printStackTrace();
		}
	}

}