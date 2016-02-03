package carsandco.tools;

public class Station {
	
	private String city;
	private double lat;
	private double lon;
	
	public Station (String city, double lat, double lon) {
		this.city = city;
		this.lat = lat;
		this.lon = lon;
	}
	
	public void setCity (String city) {
		this.city = city;
	}
	
	public void setLat (double lat) {
		this.lat = lat;
	}
	
	public void setLong (double lon) {
		this.lon = lon;
	}
	
	public String getCity() {
		return city;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}

}
