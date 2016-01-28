package carsandco.management;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;

import com.google.gson.Gson;

import carsandco.tools.MongoClass;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.capitol.api.client.AccidentApiClient;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class SendBill implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		RepairContract contract = (RepairContract) execution.getVariable("contract");
		InvoiceRequest invoice = (InvoiceRequest) execution.getVariable("invoice");

		Gson gson = new Gson();
		String newInvoice = gson.toJson(invoice);
		System.out.println("Final JSON-Invoice to send:");
		System.out.println(newInvoice);
		
		String customerID = Integer.toString(contract.getCustomerID());
		
		JSONObject capitol = MongoClass.getJSON("customers", "name", "Capitol");
		JSONObject bvis = MongoClass.getJSON("customers", "name", "BVIS");
		
		if (customerID.equals(capitol.getString("id"))) {
			AccidentApiClient capitol_api = new AccidentApiClient();
			capitol_api.continueAccident(invoice);
		}

		if (customerID.equals(bvis.getString("id"))) {
			//TODO Update BVIS URL to send invoice
			URL url = new URL("BVIS-Invoice-Endpoint");
			try {
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
				out.write(newInvoice);
				out.close();

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

				while (in.readLine() != null) {
				}
				System.out.println("REST Service Invoked Successfully..");
				in.close();
			} catch (Exception e) {
				System.out.println("Error while calling REST Service");
				e.printStackTrace();
			}
		} else {
			System.out.println("Error sending invoice " + invoice.getPurpose() 
								+ " with Transaction Key " + invoice.getTransactionKey() 
								+ ". No URL/Debtor could be found.");
		}
		
	}

}
