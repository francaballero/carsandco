package carsandco.headquarter;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import com.google.gson.Gson;

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

@Path("/new")
public class ReceiveContract {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();

		@POST
		@Consumes(MediaType.APPLICATION_JSON)
		public void incomingContractHandler(InputStream incomingData) {
			String contractJson = "";

			try {
//Receive POST data (RepairContract)				
				BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));			
				Gson gson = new Gson();
				RepairContract newContract = gson.fromJson(input, RepairContract.class);
				contractJson = gson.toJson(newContract);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("contract", newContract);
				System.out.println("New RepairContract received: \n" + contractJson);
//Start camunda process with contract process variable				
				ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("contract", newContract.getTransactionKey(), map);
				System.out.println("Process startet with ID: " + processInstance.getId());
				
			} catch (Exception e) {
				System.err.println("Error Parsing new Contract: - ");
				e.printStackTrace();
			}
			
		}
}
