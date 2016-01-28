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
import javax.ws.rs.core.Response;

@Path("/contract")
public class ReceiveContract {

		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		RuntimeService runtimeService = processEngine.getRuntimeService();

		@POST
		@Path("/new")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response incomingContractHandler(InputStream incomingData) {
			String contractJson = "";
			//StringBuilder builder = new StringBuilder();
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(incomingData));
				/*String line = null;
				while ((line = in.readLine()) != null) {
					builder.append(line);
				}
				String contractJson = builder.toString();*/
				
				Gson gson = new Gson();
				RepairContract newContract = gson.fromJson(input, RepairContract.class);
				contractJson = gson.toJson(newContract);
				
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("contract", newContract);
				
				ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("contract", newContract.getTransactionKey(), map);
				
				System.out.println("New contract data received: \n" + contractJson);
				System.out.println("Process startet with ID: " + processInstance.getId());
				
			} catch (Exception e) {
				System.out.println("Error Parsing new Contract: - ");
				e.printStackTrace();
			}
			
			// return HTTP response 200 in case of success
			return Response.status(200).entity(contractJson).build();
		}
}
