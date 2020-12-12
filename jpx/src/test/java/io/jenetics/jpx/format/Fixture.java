package io.jenetics.jpx.format;

import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Longitude;

import static io.jenetics.jpx.Length.Unit.METER;

abstract class Fixture {

	protected LocationFormatter f;

	protected Location latitude(double d){ return Location.of(Latitude.ofDegrees(d)); }
	protected Location latitude(double d, double m){ return latitude(d + m/60.0); }
	protected Location latitude(double d, double m, double s){ return latitude(d + m/60.0 + s/3600.0); }

	protected Location longitude(double d){ return Location.of(Longitude.ofDegrees(d)); }
	protected Location longitude(double d, double m){ return longitude(d + m/60.0); }
	protected Location longitude(double d, double m, double s){ return longitude(d + m/60.0 + s/3600.0); }

	protected Location elevation(double e){ return Location.of(Length.of(e, METER)); }

	protected Location location(double lat, double lon, double ele){
		return Location.of(Latitude.ofDegrees(lat), Longitude.ofDegrees(lon), Length.of(ele, METER));
	}

}
