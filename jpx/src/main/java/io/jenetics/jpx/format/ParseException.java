/*
 * Java GPX Library (@__identifier__@).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jenetics.jpx.format;

import static java.lang.String.format;

import java.io.Serial;

/**
 * This exception is thrown when the parsing of a location pattern string fails.
 *
 * @see LocationFormatter
 *
 * @version 2.2
 * @since 2.2
 */
public class ParseException extends FormatterException {

	@Serial
	private static final long serialVersionUID = 1;

	/**
	 * Create a new parse exception with the given parameters.
	 *
	 * @param message the error message
	 * @param in the erroneous location string
	 * @param position the error position
	 */
	ParseException(
		final String message,
		final CharSequence in,
		final int position
	) {
		super(format("%s at position %d in '%s'.", message, position, in));
	}

}
