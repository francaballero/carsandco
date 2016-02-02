package carsandco.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;

import com.google.gson.Gson;

import carsandco.tools.JsonHandler;
import de.uniko.digicom.carsandco.messages.PaymentNotification;

@Path("/payment")
public class ReceivePaymentNotification {
	
	private static final Logger LOGGER = Logger.getLogger(ReceivePaymentNotification.class);
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	RuntimeService runtimeService = processEngine.getRuntimeService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response incomingContractHandler(InputStream incomingData) {
		String paymentNoteJson = "";
		try {
			// Receive POST data (PaymentNotification)
			BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));
			PaymentNotification paymentNote = new Gson().fromJson(input, PaymentNotification.class);
			paymentNoteJson = JsonHandler.toJson(paymentNote);
			LOGGER.info("Payment Notification received: \n" + JsonHandler.printJSON(paymentNoteJson));
			// Continue camunda execution
			runtimeService.createMessageCorrelation("payment")
					.processInstanceBusinessKey(paymentNote.getTransactionKey())
					.setVariable("paymentNotification", paymentNoteJson).correlate();
			return Response.status(200).entity(paymentNoteJson).build();
		} catch (Exception e) {
			LOGGER.error("Error Parsing Payment Notification: - ");
			e.printStackTrace();
			return Response.status(404).entity(paymentNoteJson).build();
		}
	}
}
