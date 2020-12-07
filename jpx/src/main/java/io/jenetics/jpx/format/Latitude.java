package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the latitude value of a given location
 * object. The latitude value is returned in degrees.
 */
class Latitude extends Field {

	Latitude(String pattern){super(pattern);}

	char type() { return 'L'; }

	boolean isLatitude() { return true; }

	/** parse latitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder builder) throws ParseException {
		double d = parseDouble(in, pos);
		builder.addLatitude(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map(v -> nf.format(v) );
	}

}
