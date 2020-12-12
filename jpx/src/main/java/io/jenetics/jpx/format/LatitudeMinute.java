package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * This field allows to access the absolute value of the minute part of
 * the latitude of a given location.
 */
class LatitudeMinute extends Field {

	LatitudeMinute(String pattern){super(pattern);}

	char type() { return 'M'; }

	void setTruncate(boolean b){ nf.setRoundingMode(b ? DOWN : HALF_EVEN); }

	/** parse latitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLatitudeMinute(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> toMinutes(d) )
			.map( d -> nf.format(d) );
	}

}
