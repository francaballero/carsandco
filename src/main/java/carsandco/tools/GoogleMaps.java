package carsandco.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleMaps {

	// This function return the closest Cars&Co station from a origin city and
	// the distance.
	public static Pair<Integer, Station> getClosestStation(String origin) throws Exception {
		List<Station> stations = getStations();
		List<Pair<Integer, Station>> distances = new ArrayList<Pair<Integer, Station>>();
		int distance;
		boolean exception = false;

		// We calculate the distances from the origin to all the stations
		for (Station station : stations) {
			try {
				distance = getDistance(origin, station.getCity());
			} catch (Exception e) {
				distance = Integer.MAX_VALUE;
				exception = true;
			}
			distances.add(new Pair<Integer, Station>(distance, station));
		}

		if (exception) {
			System.out.println("Error calculating the closest service station.");
			System.out.println("Contract handeled by headquarter in Amsterdam.\n");
			MongoClass.closeDatabaseConnection();
		}

		// We sort the List by distances
		Collections.sort(distances, new CustomComparator());

		// And we return the name of the closest station
		return distances.get(0);
	}

	// This function returns a List with all the stations loaded from the DB.
	private static List<Station> getStations() throws UnknownHostException, JSONException {
		JSONObject stationsJsonObject = MongoClass.getJSON("stations", "id", "1");

		JSONArray stationsJsonArray = stationsJsonObject.getJSONArray("stations");

		List<Station> stationsArray = new ArrayList<Station>();
		String city;
		double lat, lon;

		for (int i = 0; i < stationsJsonArray.length(); i++) {
			city = (String) stationsJsonArray.getJSONObject(i).getString("city");
			lat = (double) stationsJsonArray.getJSONObject(i).getDouble("lat");
			lon = (double) stationsJsonArray.getJSONObject(i).getDouble("long");
			stationsArray.add(new Station(city, lat, lon));
		}

		return stationsArray;
	}

	// This function returns a GoogleMap JsonObject with information between two
	// cities.
	private static JSONObject getGoogleMapJSON(String origin, String destination) throws IOException, JSONException {
		try {
			String line, outputString = "";
			URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin
					+ "&destinations=" + destination);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				outputString += line;
			}
			return new JSONObject(outputString);
		} catch (Exception e) {
			System.out.println("Error in calling to Google API");
			MongoClass.closeDatabaseConnection();
			e.printStackTrace();
			return null;
		}
	}

	// This function returns the distance (in meters) between two cities.
	private static int getDistance(String origin, String destination) throws IOException, JSONException {
		// We read the GoogleMap JSON output to return the distance in meters
		JSONObject json = getGoogleMapJSON(origin, destination);
		JSONArray rows = json.getJSONArray("rows");
		JSONObject elements = rows.getJSONObject(0);
		JSONArray elements1 = elements.getJSONArray("elements");
		JSONObject distances = elements1.getJSONObject(0);
		JSONObject distances1 = distances.getJSONObject("distance");
		return distances1.getInt("value");
	}
}
