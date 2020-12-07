package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static io.jenetics.jpx.Length.Unit.METER;
import static java.lang.Math.abs;

/**
 * This field allows to access the absolute elevation (in meter) of a
 * given location.
 */
class ElevationMeter extends Field {

	ElevationMeter(String pattern){super(pattern);}

	char type() { return 'H'; }

	boolean isElevation() { return true; }

	/** parse elevation as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addElevation(d);
	}

	@Override public Optional<String> format( Location loc) {
		return loc.elevation()
			.map( l -> abs(l.to(METER)) )
			.map( v -> nf.format(v) );
	}

}
