package io.jenetics.jpx.format;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.Optional;

import static java.lang.Math.*;

/**
 * Represents one of the existing location fields: latitude, longitude and
 * elevation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
abstract class Field implements Format {

	protected final static DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);

	protected NumberFormat nf;
	protected final String pattern;

	protected Field(String pattern){
		String decimalPattern = toDecimalPattern(pattern);
		this.nf = new DecimalFormat(decimalPattern, symbols);
		this.pattern = pattern;
	}

	void setPrefixSign(boolean b){}

	abstract char type();

	protected double toMinutes(double degrees) {
		double dd = abs(degrees);
		return (dd - floor(dd)) * 60.0;
	}

	protected double toSeconds(double degrees) {
		double dd = abs(degrees);
		double d = floor(dd);
		double m = floor((dd - d) * 60.0);
		return (dd - d - m / 60.0) * 3600.0;
	}

	static Optional<Field> ofPattern(String pattern) {
		// TODO better?
		for (int i = 0; i < pattern.length(); ++i) {
			char c = pattern.charAt(i);
			switch (c){
				case 'L': {
					String p = pattern.replace('L', 'D');
					return Optional.of(new LatitudeDegree(p));
				}
				case 'D': return Optional.of(new LatitudeDegree(pattern));
				case 'M': return Optional.of(new LatitudeMinute(pattern));
				case 'S': return Optional.of(new LatitudeSecond(pattern));
				case 'l': {
					String p = pattern.replace('l','d');
					return Optional.of(new LongitudeDegree(p));
				}
				case 'd': return Optional.of(new LongitudeDegree(pattern));
				case 'm': return Optional.of(new LongitudeMinute(pattern));
				case 's': return Optional.of(new LongitudeSecond(pattern));
				case 'E': return Optional.of(new Elevation(pattern));
				case 'H': {
					String p = pattern.replace('H', 'E');
					return Optional.of(new Elevation(p));
				}
			}
		}
		return Optional.empty();
	}

	protected String toDecimalPattern(String pattern) { return pattern.replace(type(), '0'); }

	@Override public String toPattern() { return pattern; }

	protected double parseDouble(CharSequence in, ParsePosition pos){
		int i = pos.getIndex();
		String s = in.toString();
		boolean strictWidth = 1 < nf.getMinimumIntegerDigits(); //better?
		if(strictWidth) {
			int end = i + toPattern().length(); // toPattern() rather than pattern because LatitudeDegree.toPattern()
			s = in.subSequence(0, end).toString(); // don't eat more digits
		}
		Number n = nf.parse(s, pos);//Does not throw an exception; if no object can be parsed, index is unchanged.
		if(i==pos.getIndex()) {
			pos.setErrorIndex(i);
			throw new ParseException("Not found " + pattern, in, i);
		}
		double d = n.doubleValue();
		return d;
	}
}
