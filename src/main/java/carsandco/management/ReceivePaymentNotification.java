package carsandco.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;

import com.google.gson.Gson;

import de.uniko.digicom.carsandco.messages.PaymentNotification;

@Path("/payment")
public class ReceivePaymentNotification {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	RuntimeService runtimeService = processEngine.getRuntimeService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void incomingContractHandler(InputStream incomingData) {
		String paymentNoteJson = "";
		try {
//Receive POST data (PaymentNotification)
			BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));
			Gson gson = new Gson();
			PaymentNotification paymentNote = gson.fromJson(input, PaymentNotification.class);
			paymentNoteJson = gson.toJson(paymentNote);
			
			System.out.println("Payment Notification received:");
			System.out.println(paymentNoteJson);
//Continue camunda execution			
			runtimeService.createMessageCorrelation("payment")
			.processInstanceBusinessKey(paymentNote.getTransactionKey())
			.setVariable("paymentNotification", paymentNote).correlate();
			System.out.println("Process with business key: " + paymentNote.getTransactionKey() + " continued.");
			
			
		} catch (Exception e) {
			System.err.println("Error Parsing Payment Notification: - ");
			e.printStackTrace();
		}
		
	}
}
