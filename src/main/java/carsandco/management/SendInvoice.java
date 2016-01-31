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
import de.uniko.digicom.capitol.api.RestResponse;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;
import de.uniko.digicom.capitol.api.client.AccidentApiClient;
import de.uniko.digicom.carsandco.messages.RepairContract;

public class SendInvoice implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
//Get current process variables
		RepairContract contract = (RepairContract) execution.getVariable("contract");
		InvoiceRequest invoice = (InvoiceRequest) execution.getVariable("invoice");
//Convert invoice to JSON String
		Gson gson = new Gson();
		String newInvoice = gson.toJson(invoice);
		
//Compare customerIDs to find out who to send the invoice
		String customerID = contract.getCustomerID();		
		JSONObject capitol = MongoClass.getJSON("customers", "name", "Capitol");
		JSONObject bvis = MongoClass.getJSON("customers", "name", "BVIS");
		//Case Capitol
		RestResponse response = null;
		if (customerID.equals(capitol.getString("_id"))) {
			System.out.println("Sending InvoiceRequest to Capitol Inc. ...");
			AccidentApiClient capitol_api = new AccidentApiClient();
			response = capitol_api.continueAccident(invoice);
		}
		//Case BVIS
		if (customerID.equals(bvis.getString("_id"))) {
			//TODO Update BVIS URL to send invoice
			System.out.println("Sending InvoiceRequest to BVIS Inc. ...");
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
				in.close();
			} catch (Exception e) {
				System.err.println("Error while calling REST Service");
				e.printStackTrace();
			}
		} else {
			System.err.println("Error sending invoice " + invoice.getPurpose() 
								+ " with Transaction Key " + invoice.getTransactionKey() 
								+ ". No URL/Debtor could be found.");
		}
		System.out.println(response.getMessage());
		
	}

}
