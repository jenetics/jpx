package io.jenetics.jpx.format;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

class LocationBuilder {

	LocationBuilder(){}

	private int latitudeSign = +1;
	void setLatitudeSign(int i) { latitudeSign = i; }

	private Double latitude = null; // degrees
	public void addLatitude(double d) {
		if(latitude==null) latitude = 0.0;
		latitude += d;
	}
	public void addLatitudeMinute(double d) { addLatitude(d/60.0); }
	public void addLatitudeSecond(double d) { addLatitude( d/3600.0); }

	private int longitudeSign = +1;
	public void setLongitudeSign(int i) { longitudeSign = i; }

	private Double longitude = null; // degrees
	public void addLongitude(double d) {
		if(longitude==null) longitude = 0.0;
		longitude += d;
	}
	public void addLongitudeMinute(double d) { addLongitude(d/60.0); }
	public void addLongitudeSecond(double d) { addLongitude(d/3600.0); }

	private int elevationSign = +1;
	void setElevationSign(int i) { elevationSign = i; }

	private Double elevation = null; // meters
	public void addElevation(double d) {
		if(elevation==null) elevation=0.0;
		elevation += d;
	}

	private Length.Unit elevationUnit = Length.Unit.METER;

	Location build(){
		Latitude lat = latitude==null ? null : Latitude.ofDegrees(latitudeSign * latitude);
		Longitude lon = longitude==null ? null : Longitude.ofDegrees(longitudeSign * longitude);
		Length ele = elevation==null ? null : Length.of(elevationSign * elevation, elevationUnit);
		return Location.of(lat, lon, ele);
	}

	@Override public String toString(){ return build().toString(); }

}
