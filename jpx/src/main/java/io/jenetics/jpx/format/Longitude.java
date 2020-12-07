package io.jenetics.jpx.format;


import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the longitude value of a given location
 * object. The longitude value is returned in degrees.
 */
class Longitude extends Field {

	Longitude(String pattern){super(pattern);}

	char type() { return 'l'; }

	boolean isLongitude() { return true; }

	/** parse longitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLongitude(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.longitude()
			.map( lon -> lon.toDegrees() )
			.map(v -> nf.format(v));
	}

}
