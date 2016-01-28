package carsandco.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.json.JSONObject;

import com.google.gson.Gson;

import carsandco.tools.MongoClass;
import de.uniko.digicom.carsandco.messages.PaymentNotification;

@Path("/contract")
public class ReceivePaymentNotification {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	RuntimeService runtimeService = processEngine.getRuntimeService();

	@POST
	@Path("/payment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response incomingContractHandler(InputStream incomingData) {
		String paymentNoteJson = "";
		//StringBuilder builder = new StringBuilder();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));
			/*String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
			String contractJson = builder.toString();*/
			
			Gson gson = new Gson();
			PaymentNotification paymentNote = gson.fromJson(input, PaymentNotification.class);
			paymentNoteJson = gson.toJson(paymentNote);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("paymentNotification", paymentNote);
			
			JSONObject transaction = new JSONObject(MongoClass.getJSON("transactions", "transactionkey", paymentNote.getTransactionKey()));
			String processID = transaction.getString("processID");
			
			//TODO How to continue camunda processes by intermediate message event
			runtimeService.messageEventReceived("contract", processID);
			//ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("contract", map);
			
			System.out.println("Payment Notification received:");
			System.out.println(paymentNoteJson);
			System.out.println("Continuing process with ID: " + processID);
			
		} catch (Exception e) {
			System.out.println("Error Parsing Payment Notification: - ");
			e.printStackTrace();
		}
		
		// return HTTP response 200 in case of success
		return Response.status(200).entity(paymentNoteJson).build();
	}
}
