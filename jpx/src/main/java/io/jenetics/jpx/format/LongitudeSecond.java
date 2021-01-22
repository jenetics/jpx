package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the absolute value of the second part of
 * the longitude of a given location.
 */
class LongitudeSecond extends Field {

	LongitudeSecond(String pattern){super(pattern);}

	char type() { return 's'; }

	/** parse longitude seconds as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLongitudeSecond(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.longitude()
			.map( lon -> lon.toDegrees() )
			.map( d -> toSeconds(d) )
			.map( d -> nf.format(d) );
	}

}
