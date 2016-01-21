package carsandco.headquarter;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;

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
			StringBuilder builder = new StringBuilder();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
				String line = null;
				while ((line = in.readLine()) != null) {
					builder.append(line);
				}
				
				//TODO: Convert JSON to RepairContract Object
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("contract", builder.toString());
				ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("contract", map);
				
				System.out.println("Data Received: " + builder.toString());
				System.out.println("Process ID: " + processInstance.getId());
				
			} catch (Exception e) {
				System.out.println("Error Parsing: - ");
			}
			
			// return HTTP response 200 in case of success
			return Response.status(200).entity(builder.toString()).build();
		}
}
