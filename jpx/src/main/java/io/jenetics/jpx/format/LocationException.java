/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
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
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.format;

/**
 * This exception is thrown if formatting or parsing of a location object fails.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class LocationException extends RuntimeException {
	private static final long serialVersionUID = 1;

	/**
	 * Create a new exception with {@code null} as its detail message.
	 */
	public LocationException() {
	}

	/**
	 * Create a new exception with the given detail {@code message}.
	 *
	 * @param message the detail message
	 */
	public LocationException(final String message) {
		super(message);
	}

	/**
	 * Create a new exception with the given detail {@code message} and
	 * exception {@code cause}.
	 *
	 * @param message the detail message
	 * @param cause the exception cause
	 */
	public LocationException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Create a new exception with the given exception {@code cause}.
	 *
	 * @param cause the exception cause
	 */
	public LocationException(final Throwable cause) {
		super(cause);
	}

}
