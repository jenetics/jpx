package io.jenetics.jpx.format;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Optional;

import static io.jenetics.jpx.Length.Unit.METER;

/**
 * This field allows to access the elevation (in meter) of a given
 * location.
 */
class Elevation extends Field {

	private boolean prefixSign = false;

	Elevation(String pattern){super(pattern);}

	void setPrefixSign(boolean b){
		prefixSign = b;
		String decimalPattern = toDecimalPattern(pattern);
		String p = b ? ("+" + decimalPattern + ";" + "-" + decimalPattern) :  decimalPattern;
		nf = new DecimalFormat(p, symbols);
	}

	char type() { return 'E'; }

	/** parse elevation as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in,pos);
		b.setElevation(d);
	}

	@Override public Optional<String> format( Location loc) {
		return loc.elevation()
			.map( l -> l.to(METER) )
			.map( d -> nf.format(d) );
	}

	@Override public String toPattern(){ return prefixSign ? "+" + pattern : pattern; }

}
