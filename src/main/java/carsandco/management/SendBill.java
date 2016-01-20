package carsandco.management;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import com.google.gson.Gson;

import de.uniko.digicom.capitol.api.Urls;
import de.uniko.digicom.capitol.api.accident.InvoiceRequest;

public class SendBill implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		InvoiceRequest invoice = (InvoiceRequest) execution.getVariable("invoice");
		Gson gson = new Gson();
		String newInvoice = gson.toJson(invoice);
		System.out.println("Final JSON-Invoice to send:");
		System.out.println(newInvoice);
		
		try {
			//url = "http://camunda-capitol.iwvi.uni-koblenz.de:8080/rest-api/accident/invoice/"
			URL url = new URL(Urls.URL_ACCIDENT_INVOICE);
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
			System.out.println(e);
		}
	}

}
