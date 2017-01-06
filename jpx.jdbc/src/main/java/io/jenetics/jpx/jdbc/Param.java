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
package io.jenetics.jpx.jdbc;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents a query parameter with <em>name</em> and <em>value</em>. Tbe
 * parameter value is evaluated lazily.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Param {

	private final String _name;
	private final List<Object> _values;

	private Param(final String name, final List<Object> values) {
		_name = requireNonNull(name);
		_values = requireNonNull(values);
	}

	/**
	 * Return the parameter name.
	 *
	 * @return the parameter name
	 */
	String name() {
		return _name;
	}

	List<Object> values() {
		return _values;
	}

	/**
	 * Create a new query parameter object from the given {@code name} and
	 * {@code value}.
	 *
	 * @param name the parameter name
	 * @param value the parameter value
	 * @return a new query parameter object
	 * @throws NullPointerException if the given parameter {@code name} is
	 *         {@code null}
	 */
	public static Param value(final String name, final Object value) {
		return new Param(name, singletonList(value));
	}

	public static Param values(final String name, final Object... values) {
		return new Param(name, asList(values));
	}

	public static Param values(final String name, final List<Object> values) {
		return new Param(name, values);
	}

	/*
	public static <T> Param insert(
		final String name,
		final SQL.Supplier<T> value
	) {
		return new Param(name, SQL.Lazy.of(value));
	}
	*/
}
