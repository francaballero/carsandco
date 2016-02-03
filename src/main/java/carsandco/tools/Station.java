package carsandco.tools;

public class Station {
	
	private String city;
	private double lat;
	private double long_;
	
	public Station (String city, double lat, double long_) {
		this.city = city;
		this.lat = lat;
		this.long_ = long_;
	}
	
	public void setCity (String city) {
		this.city = city;
	}
	
	public void setLat (double lat) {
		this.lat = lat;
	}
	
	public void setLong (double long_) {
		this.long_ = long_;
	}
	
	public String getCity() {
		return city;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLong() {
		return long_;
	}

}
