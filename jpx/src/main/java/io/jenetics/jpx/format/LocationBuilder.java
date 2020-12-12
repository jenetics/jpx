package io.jenetics.jpx.format;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

class LocationBuilder {

	private int latitudeSign = +1;
	private Double latitude = null; // degrees
	private int longitudeSign = +1;
	private Double longitude = null; // degrees
	private Double elevation = null; // meters
	private Length.Unit elevationUnit = Length.Unit.METER;

	LocationBuilder(){}

	LocationBuilder copy(){
		LocationBuilder c = new LocationBuilder();
		c.latitudeSign = latitudeSign;
		c.latitude = latitude;
		c.longitudeSign = longitudeSign;
		c.longitude = longitude;
		c.elevation = elevation;
		return c;
	}

	void copy(LocationBuilder from){
		latitudeSign = from.latitudeSign;
		latitude = from.latitude;
		longitudeSign = from.longitudeSign;
		longitude = from.longitude;
		elevation = from.elevation;
	}

	void setLatitudeSign(int i) { latitudeSign = i; }

	void addLatitude(double d) {
		if(latitude==null) latitude = 0.0;
		latitude += d;
	}
	void addLatitudeMinute(double d) { addLatitude(d/60.0); }
	void addLatitudeSecond(double d) { addLatitude( d/3600.0); }

	void setLongitudeSign(int i) { longitudeSign = i; }

	void addLongitude(double d) {
		if(longitude==null) longitude = 0.0;
		longitude += d;
	}
	void addLongitudeMinute(double d) { addLongitude(d/60.0); }
	void addLongitudeSecond(double d) { addLongitude(d/3600.0); }

	void setElevation(double d) { elevation = d; }

	Location build(){
		Latitude lat = latitude==null ? null : Latitude.ofDegrees(latitudeSign * latitude);
		Longitude lon = longitude==null ? null : Longitude.ofDegrees(longitudeSign * longitude);
		Length ele = elevation==null ? null : Length.of(elevation, elevationUnit);
		return Location.of(lat, lon, ele);
	}

	@Override public String toString(){ return build().toString(); }

}
