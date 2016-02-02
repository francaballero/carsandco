package carsandco.headquarter;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import com.google.gson.Gson;

import carsandco.tools.JsonHandler;
import de.uniko.digicom.carsandco.messages.RepairContract;

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

@Path("/new")
public class ReceiveContract {
		private static final Logger LOGGER = Logger.getLogger(ReceiveContract.class);
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();

		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		public Response incomingContractHandler(InputStream incomingData) {
			String contractJson = "";

			try {
//Receive POST data (RepairContract)				
				BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));			
				Gson gson = new Gson();
				RepairContract newContract = gson.fromJson(input, RepairContract.class);
				contractJson = gson.toJson(newContract);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("contract", contractJson);
				LOGGER.info("New RepairContract received: \n" + JsonHandler.printJSON(contractJson));
//Start camunda process with contract process variable				
				ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("contract", newContract.getTransactionKey(), map);
				LOGGER.info("Process startet with ID: " + processInstance.getId());
				
			} catch (Exception e) {
				LOGGER.error("Error Parsing new Contract: - ");
				e.printStackTrace();
			}
			return Response.status(200).entity(contractJson).build();
		}
}
