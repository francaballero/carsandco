package carsandco.headquarter;

import org.apache.log4j.Logger;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import carsandco.tools.GoogleMaps;
import carsandco.tools.JsonHandler;
import carsandco.tools.Pair;
import carsandco.tools.Station;
import de.uniko.digicom.carsandco.messages.RepairContract;


public class ChooseStation implements JavaDelegate {

	private static final Logger LOGGER = Logger.getLogger( ChooseStation.class);
	
	public void execute(DelegateExecution execution) throws Exception {
		String contractJson = (String) execution.getVariable("contract");
		RepairContract contract = JsonHandler.toObject(contractJson, RepairContract.class);
		
		Station carLocationCity = new Station(contract.getCarLocationCity(), 52.374444, 9.738611);
	    String carLocationCityString = JsonHandler.toJson(carLocationCity);
	    LOGGER.info(carLocationCityString);
	    
	    LOGGER.info("The contract shows that the car is in " + carLocationCity.getCity());
	    LOGGER.info("Calculating the closest 'Cars&Co' station...");
			
		Pair<Integer, Station> station = GoogleMaps.getClosestStation(carLocationCity.getCity());
		String googleMapsLink = "https://www.google.com/maps/dir/" + carLocationCity.getCity() + "/" + station.getValue().getCity();

		String closestStationString = JsonHandler.toJson(station.getValue());
		LOGGER.info(closestStationString);
		
		execution.setVariable("carCity", carLocationCityString);
		execution.setVariable("closestStation", closestStationString);
		
			
		LOGGER.info("The closest station to '" +carLocationCity.getCity() + "' is: '" + station.getValue().getCity() + "'."
				+ "\nIn a distance of: " + station.getKey()/1000.00 +"km.\n\n"
				+ googleMapsLink + "/\n\n\n");
		LOGGER.info("Redirecting contract information to service station...");	

	}
	
	

}
