package carsandco.headquarter;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.JSONException;

import carsandco.tools.GoogleMaps;
import carsandco.tools.JsonHandler;
import carsandco.tools.Pair;
import de.uniko.digicom.carsandco.messages.RepairContract;


public class ChooseStation implements JavaDelegate {

	private static final Logger LOGGER = Logger.getLogger( ChooseStation.class);
	
	public void execute(DelegateExecution execution) throws UnknownHostException, JSONException {
		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		
	    String origin =  contract.getCarLocationCity();
	    
	    LOGGER.info("\n\n\nThe contract shows that the car is in '" + origin);
	    LOGGER.info("Calculating the closest 'Cars&Co' station...\n");
	    
	    try{
		Pair<Integer, String> station = GoogleMaps.getClosestStation(origin);
		String googleMapsLink = "https://www.google.com/maps/dir/" + origin + "/" + station.getValue();
		execution.setVariable("googleMapsLink", googleMapsLink);
			
		LOGGER.info("The closest station to '" + origin + "' is: '" + station.getValue() + "'."
				+ "In a distance of: " + station.getKey()/1000.00 +"km.");
	
	    } catch(Exception e){
	    	LOGGER.error("Calculating the closest station failed. Could not invoke Google API.");
	    	e.printStackTrace();
	    }
	}
	
	

}
