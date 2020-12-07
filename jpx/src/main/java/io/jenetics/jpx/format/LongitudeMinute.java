package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the absolute value of the minute part of
 * the longitude of a given location.
 */
class LongitudeMinute extends Field {

	LongitudeMinute(String pattern){super(pattern);}

	char type() { return 'm'; }

	boolean isLongitude() { return true; }

	/** parse longitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLongitudeMinute(d);
	}

	// TODO round or truncate
	@Override public Optional<String> format(Location loc) {
		return loc.longitude()
			.map( lon -> lon.toDegrees() )
			.map( d -> toMinutes(d) )
			.map(v -> nf.format(v));
	}

}
