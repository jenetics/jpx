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

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * Represents a table column with a name and a mapping function, which converts
 * a given value to the needed column type.
 *
 * @param <T> raw object type
 * @param <C> column object type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Column<T, C> {

	/**
	 * Return the column name.
	 *
	 * @return the column name
	 */
	public String name();

	/**
	 * Return the mapper function with maps the raw type {@code T} to the column
	 * type which is insert into the DB.
	 *
	 * @return raw-type to column-type mapper
	 */
	public Function<T, C> mapper();


	/**
	 * Create a column object with the given values.
	 *
	 * @param name the column name
	 * @param mapper the raw-type to column-type mapper
	 * @param <T> raw object type
	 * @param <C> column object type
	 * @return a new column object
	 * @throws NullPointerException if one of the parameters is {@code null}
	 */
	public static <T, C> Column<T, C>
	of(final String name, final Function<T, C> mapper) {
		requireNonNull(name);
		requireNonNull(mapper);

		return new Column<T, C>() {
			@Override
			public String name() {
				return name;
			}

			@Override
			public Function<T, C> mapper() {
				return mapper;
			}
		};
	}

	/**
	 * Create a column objects with the given name.
	 *
	 * @param name the column name
	 * @param <T> the raw-object and column-type
	 * @return a new column object
	 */
	public static <T> Column<T, T> of (final String name) {
		return of(name, Function.identity());
	}

}
