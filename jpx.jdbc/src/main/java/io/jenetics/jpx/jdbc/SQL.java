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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package io.jenetics.jpx.jdbc;

import java.sql.SQLException;

/**
 * Contains implementations of existing functional classes, which allow to throw
 * a {@link SQLException}.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SQL {

	private SQL() {
	}

	/**
	 * Represents a supplier of results.
	 *
	 * @see java.util.function.Supplier
	 *
	 * @param <T> the result type
	 */
	@FunctionalInterface
	public static interface Supplier<T> {
		public T get() throws SQLException;
	}

	/**
	 * Represents a function that accepts one argument and produces a result.
	 *
	 * @see java.util.function.Function
	 *
	 * @param <T> the argument type
	 * @param <R> the result type
	 */
	@FunctionalInterface
	public static interface Function<T, R> {
		public R apply(final T value) throws SQLException;
	}

	/**
	 * Represents an operation that accepts a single input argument and returns
	 * no result. Unlike most other functional interfaces, Consumer is expected
	 * to operate via side-effects.
	 *
	 * @see java.util.function.Consumer
	 *
	 * @param <T> the argument type
	 */
	@FunctionalInterface
	public static interface Consumer<T> {
		public void accept(final T value) throws SQLException;
	}

}
