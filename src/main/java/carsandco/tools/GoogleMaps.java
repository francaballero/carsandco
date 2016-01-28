package carsandco.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMaps {
	
	//This function return the closest Cars&Co station from a origin city and the distance.
	public static Pair<Integer, String> getClosestStation(String origin) throws UnknownHostException, JSONException {
		List<String> stations = getStations();
		List<Pair<Integer,String>> distances = new ArrayList<Pair<Integer,String>>();
		int distance;
		boolean exception = false;
		
		//We calculate the distances from the origin to all the stations 
		for (String station : stations) {
			try {
				distance = getDistance(origin, station);
			} catch (Exception e) {
				distance = Integer.MAX_VALUE;
				exception = true;
			}
			distances.add(new Pair<Integer, String>(distance, station));
		}
		
		if (exception) {
			System.out.println("Error calculating the closest service station.");
	    	System.out.println("Contract handeled by headquarter in Amsterdam.\n");
		}
		
		//We sort the List by distances
		distances.sort(new Comparator<Pair<Integer, String>>() {
	        public int compare(Pair<Integer, String> o1, Pair<Integer, String> o2) {
	            if (o1.getKey() < o2.getKey()) {
	                return -1;
	            } else if (o1.getValue().equals(o2.getValue())) {
	                return 0;
	            } else {
	                return 1;
	            }
	        }
	    });
		
		//And we return the name of the closest station
		return distances.get(0);
	}
	
	//This function returns a List with all the stations loaded from the DB.
	private static List<String> getStations() throws UnknownHostException, JSONException {
		JSONObject stationsJsonObject = MongoClass.getJSON("stations", "id", "1");
		JSONArray stationsJsonArray = stationsJsonObject.getJSONArray("stations");
				
		List<String> stationsArray = new ArrayList<String>();
				
		for(int i = 0; i < stationsJsonArray.length(); i++) {
			stationsArray.add(stationsJsonArray.getString(i));
		}
				
		return stationsArray;
	}
	
	//This function returns a GoogleMap JsonObject with information between two cities.
	private static JSONObject getGoogleMapJSON(String origin, String destination) throws IOException, JSONException {
		String line, outputString = "";
		URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins="+origin+"&destinations="+destination);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader reader = new BufferedReader(
		new InputStreamReader(conn.getInputStream()));
		while ((line = reader.readLine()) != null) {
		     outputString += line;
		}
		return new JSONObject (outputString);
	}
	
	//This function returns the distance (in meters) between two cities.
	private static int getDistance (String origin, String destination) throws IOException, JSONException {
		//We read the GoogleMap JSON output to return the distance in meters
		JSONObject json = getGoogleMapJSON (origin, destination);
		JSONArray rows = json.getJSONArray("rows");
		JSONObject elements = rows.getJSONObject(0);
		JSONArray elements1 = elements.getJSONArray("elements");
		JSONObject distances = elements1.getJSONObject(0);
		JSONObject distances1 = distances.getJSONObject("distance");
		return distances1.getInt("value");
	}
}
