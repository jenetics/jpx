package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

import static io.jenetics.jpx.Length.Unit.METER;

/**
 * This field allows to access the elevation (in meter) of a given
 * location.
 */
class Elevation extends Field {

	Elevation(String pattern){super(pattern);}

	char type() { return 'E'; }

	boolean isElevation() { return true; }

	/** parse elevation as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in,pos);
		b.addElevation(d);
	}

	@Override public Optional<String> format( Location loc) {
		return loc.elevation()
			.map( l -> l.to(METER) )
			.map( v -> nf.format(v) );
	}

}
