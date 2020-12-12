package io.jenetics.jpx.format;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Optional;

import static java.lang.Math.abs;
import static java.math.RoundingMode.DOWN;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * This field allows to access the latitude
 * degrees of a given location.
 *
 * If the pattern has a fractional part, the latitude is rounded
 * to match the pattern.
 *
 * If the pattern has no fractional part, the latitude is truncated
 * rather than rounded, on the assumption that the fractional part
 * will be represented by minutes and seconds.
 *
 */
class LatitudeDegree extends Field {

	private boolean prefixSign = false;

	LatitudeDegree(String pattern){super(pattern);}

	void setPrefixSign(boolean b){
		prefixSign = b;
		String decimalPattern = toDecimalPattern(pattern);
		String p = b ? ("+" + decimalPattern + ";" + "-" + decimalPattern) :  decimalPattern;
		nf = new DecimalFormat(p, symbols);
	}
	boolean isPrefixSign(){ return prefixSign; }

	void setTruncate(boolean b){ nf.setRoundingMode(b ? DOWN : HALF_EVEN); }

	private boolean absolute = false;
	void setAbsolute(boolean b){absolute=b;}

	char type() { return 'D'; }

	/** parse latitude as double */
	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		double d = parseDouble(in, pos);
		b.addLatitude(d);
	}

	@Override public Optional<String> format(Location loc) {
		return loc.latitude()
			.map( lat -> lat.toDegrees() )
			.map( d -> absolute ? abs(d) : d )
			.map( d -> nf.format(d) );
	}

	@Override public String toPattern(){ return prefixSign ? "+" + pattern : pattern; }

}
