package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static java.lang.Math.abs;

/**
 * This field allows to access the absolute value of the latitude
 * degrees of a given location. If you need to extract the signed
 * latitude degrees, use {@link #Latitude} instead.
 *
 * If the pattern has a fractional part, the latitude is rounded
 * to match the pattern.
 *
 * If the pattern has no fractional part, the latitude is truncated
 * rather than rounded, on the assumption that the fractional part
 * will be represented by minutes and seconds.
 *
 * If the intention is to have no fractional part and no minutes and
 * seconds, use LATITUDE (L) instead.
 */
class LatitudeDegree extends Field {

	LatitudeDegree(String pattern){super(pattern);}

	char type() { return 'D'; }

	boolean isLatitude() { return true; }

	/** parse latitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLatitude(d);
	}

	// TODO must preserve leading zeroes
	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> abs(d) )
			.map( d -> hasFraction() ? d : truncate(d) )
			.map( d -> nf.format(d) );
	}

}
