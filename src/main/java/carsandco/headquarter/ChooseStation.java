package carsandco.headquarter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import carsandco.tools.GoogleMaps;
import carsandco.tools.Pair;


public class ChooseStation implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		String contract_string = (String) execution.getVariable("contract");
	    JSONObject contract = (JSONObject) new JSONParser().parse(contract_string);
		
	    String origin = (String) contract.get("city");
	    
	    System.out.println("\n\n\nThe contract shows that the car is in '" + origin + "'.\n"
				+ "Calculating the closest 'Cars&Co' station...");
		
		Pair<Integer, String> station = GoogleMaps.getClosestStation(origin);
		
		System.out.println("\nThe closest station to '" + origin + "' is: '" + station.getValue() + "'."
				+ "In a distance of: " + station.getKey()/1000.00 +"km.\n\n\n");
	    
	}

}
