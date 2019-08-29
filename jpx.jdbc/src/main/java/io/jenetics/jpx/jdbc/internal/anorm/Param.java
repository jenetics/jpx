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
package io.jenetics.jpx.jdbc.internal.anorm;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
	private final Collection<?> _values;

	private Param(final String name, final Collection<?> values) {
		_name = requireNonNull(name);
		_values = requireNonNull(values);
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
	public Collection<?> of() {
		return _values;
	}

	@Override
	public String toString() {
		return format("%s -> %s", _name, _values);
	}


	/* *************************************************************************
	 * Static factory methods.
	 * ************************************************************************/

	/**
	 * Create a new query parameter object from the given {@code name} and
	 * {@code value}.
	 *
	 * @param name the parameter name
	 * @param values the parameter values
	 * @return a new query parameter object
	 * @throws NullPointerException if the given parameter {@code name} is
	 *         {@code null}
	 */
	public static Param of(final String name, final Object... values) {
		return new Param(name, asList(values));
	}

	/**
	 * Create a new parameter object with the given name and values.
	 *
	 * @param name the parameter name
	 * @param values the parameter values
	 * @return a new parameter object
	 */
	public static Param of(
		final String name,
		final Collection<?> values
	) {
		return new Param(name, values);
	}

//	/**
//	 * Return a new parameter object with the given name and long values.
//	 *
//	 * @param name the parameter name
//	 * @param values the parameter values
//	 * @return a new parameter object
//	 */
//	public static Param of(final String name, final long... values) {
//		return new Param(
//			name,
//			LongStream.of(values)
//				.boxed()
//				.collect(Collectors.toList())
//		);
//	}

	/**
	 * Create a ne parameter object with the given name and values.
	 *
	 * @param name the parameter name
	 * @param values the raw parameter values
	 * @param mapper the parameter mapper
	 * @param <V> the raw-type
	 * @return a new parameter object
	 */
	public static <V> Param of(
		final String name,
		final Collection<? extends V> values,
		final Function<? super V, ?> mapper
	) {
		final List<?> converted = values.stream()
			.map(mapper)
			.collect(Collectors.toList());

		return new Param(name, converted);
	}

}
