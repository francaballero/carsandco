package carsandco.headquarter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import carsandco.tools.GoogleMaps;
import carsandco.tools.Pair;
import de.uniko.digicom.carsandco.messages.RepairContract;


public class ChooseStation implements JavaDelegate {

	public void execute(DelegateExecution execution) throws Exception {
		RepairContract contract = (RepairContract) execution.getVariable("contract");
		
	    String origin =  contract.getCarLocationCity();
	    
	    try{
	    System.out.println("\n\n\nThe contract shows that the car is in '" + origin + "'.\n"
				+ "Calculating the closest 'Cars&Co' station...");
		
		Pair<Integer, String> station = GoogleMaps.getClosestStation(origin);
		String googleMapsLink = "https://www.google.com/maps/dir/" + origin + "/" + station.getValue();
		execution.setVariable("googleMapsLink", googleMapsLink);
		
		System.out.println("\nThe closest station to '" + origin + "' is: '" + station.getValue() + "'."
				+ "In a distance of: " + station.getKey()/1000.00 +"km.\n\n"
				+ googleMapsLink + "/\n\n\n");
	    } catch(Exception e){
	    	System.out.println("Error calculating the closest service station.");
	    	System.out.println("Contract handeled by headquarter in Amsterdam.");
	    }
	}

}
