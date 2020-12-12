package io.jenetics.jpx.format;

import java.text.ParsePosition;
import java.util.Optional;

/** Just a '+' that is not participating in a field. */
enum Plus implements Format {

	INSTANCE;

	@Override public Optional<String> format(Location value) { return Optional.of("+"); }

	@Override public void parse(CharSequence in, ParsePosition pos, LocationBuilder b) throws ParseException {
		int i = pos.getIndex();
		if(in.length() <= i){
			pos.setErrorIndex(i);
			throw new ParseException("Cannot parse +", in, i);
		}
		char c = in.charAt(i);
		if(c != '+'){
			pos.setErrorIndex(i);
			throw new ParseException("Wanted +, found " + c, in, i);
		}
		pos.setIndex(i+1);
		// no call to builder
	}

	@Override public String toPattern() { return "+"; }

}
