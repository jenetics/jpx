package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/**
 * This field allows to access the absolute value of the second part of
 * the latitude of a given location.
 */
class LatitudeSecond extends Field {

	LatitudeSecond(String pattern){super(pattern);}

	char type() { return 'S'; }

	/** parse latitude seconds as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLatitudeSecond(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> toSeconds(d) )
			.map( d -> nf.format(d) );
	}

}
