package io.jenetics.jpx.format;

public class ParseException extends RuntimeException {
	private static final long serialVersionUID = 1;

	ParseException(String message, CharSequence in, int position){
		super(message + " at position " + position + " in " + in);
	}

}
