package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the absolute value of the minute part of
 * the latitude of a given location.
 */
class LatitudeMinute extends Field {

	LatitudeMinute(String pattern){super(pattern);}

	char type() { return 'M'; }

	boolean isLatitude() { return true; }

	/** parse latitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLatitudeMinute(d);
	}

	// TODO round or truncate
	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> toMinutes(d) )
			.map( v -> nf.format(v) );
	}

}
