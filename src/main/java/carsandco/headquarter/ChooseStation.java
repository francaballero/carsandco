package carsandco.headquarter;

import java.net.UnknownHostException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;

import carsandco.tools.GoogleMaps;
import carsandco.tools.JsonHandler;
import carsandco.tools.Pair;
import de.uniko.digicom.carsandco.messages.RepairContract;


public class ChooseStation implements JavaDelegate {

	public void execute(DelegateExecution execution) throws UnknownHostException, JSONException {
		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		
	    String origin =  contract.getCarLocationCity();
	    
	    System.out.println("\n\n\nThe contract shows that the car is in '" + origin + "'.\n"
	    		+ "Calculating the closest 'Cars&Co' station...\n");
			
		Pair<Integer, String> station = GoogleMaps.getClosestStation(origin);
		String googleMapsLink = "https://www.google.com/maps/dir/" + origin + "/" + station.getValue();
		execution.setVariable("googleMapsLink", googleMapsLink);
			
		System.out.println("The closest station to '" + origin + "' is: '" + station.getValue() + "'."
				+ "\nIn a distance of: " + station.getKey()/1000.00 +"km.\n\n"
				+ googleMapsLink + "/\n\n\n");
	
	}
	
	

}
