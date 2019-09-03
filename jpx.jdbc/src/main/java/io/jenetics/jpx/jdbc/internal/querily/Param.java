/*
 * Java GPX Library (@__identifier__@).
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
package io.jenetics.jpx.jdbc.internal.querily;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Represents a query parameter with <em>name</em> and <em>value</em>. The
 * parameter value is evaluated lazily.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Param {

	private final String _name;
	private final Object _value;

	private Param(final String name, final Object value) {
		_name = requireNonNull(name);
		_value = value;
	}

	/**
	 * Return the parameter name.
	 *
	 * @return the parameter name
	 */
	public String name() {
		return _name;
	}

	/**
	 * Return the parameter values.
	 *
	 * @return the parameter values
	 */
	public Object value() {
		return _value;
	}

	@Override
	public String toString() {
		return format("%s -> %s", _name, _value);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new query parameter object from the given {@code name} and
	 * {@code value}.
	 *
	 * @param name the parameter name
	 * @param value the parameter values
	 * @return a new query parameter object
	 * @throws NullPointerException if the given parameter {@code name} is
	 *         {@code null}
	 */
	public static Param of(final String name, final Object value) {
		return new Param(name, value);
	}

}
