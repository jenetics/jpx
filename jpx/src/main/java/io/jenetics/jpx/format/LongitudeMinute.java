package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * This field allows to access the absolute value of the minute part of
 * the longitude of a given location.
 */
class LongitudeMinute extends Field {

	LongitudeMinute(String pattern){super(pattern);}

	char type() { return 'm'; }

	void setTruncate(boolean b){ nf.setRoundingMode(b ? DOWN : HALF_EVEN); }

	/** parse longitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLongitudeMinute(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.longitude()
			.map( lon -> lon.toDegrees() )
			.map( d -> toMinutes(d) )
			.map( d -> nf.format(d) );
	}

}
