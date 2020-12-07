package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static java.lang.Math.abs;

/**
 * This field allows to access the absolute value of the longitude
 * degrees of a given location. If you need to extract the signed
 * longitude degrees, use {@link #Longitude} instead.
 *
 * If the pattern has a fractional part, the longitude is rounded
 * to match the pattern.
 *
 * If the pattern has no fractional part, the longitude is truncated
 * rather than rounded, on the assumption that the fractional part
 * will be represented by minutes and seconds.
 *
 * If the intention is to have no fractional part and no minutes and
 * seconds, use LONGITUDE (l) instead.
 */
class LongitudeDegree extends Field {

	LongitudeDegree(String pattern){super(pattern);}

	char type() { return 'd'; }

	boolean isLongitude() { return true; }

	/** parse longitude degree as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLongitude(d);
	}

	// TODO must preserve leading zeroes
	@Override public Optional<String> format(Location loc) {
		return loc.longitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> abs(d) )
			.map( d -> hasFraction() ? d : truncate(d) )
			.map( d -> nf.format(d) );
	}

}
