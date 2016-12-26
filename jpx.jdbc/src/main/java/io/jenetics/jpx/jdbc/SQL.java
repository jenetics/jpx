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

import static java.util.Objects.requireNonNull;

import java.sql.SQLException;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class SQL {
	private SQL() {
	}

	@FunctionalInterface
	public static interface Supplier<T> {
		public T get() throws SQLException;
	}

	@FunctionalInterface
	public static interface Function<T, R> {
		public R apply(final T value) throws SQLException;
	}

	@FunctionalInterface
	public static interface Consumer<T> {
		public void accept(final T value) throws SQLException;
	}

	public static final class Lazy<T> implements Supplier<T> {
		private final transient Supplier<T> _supplier;

		private T _value;
		private volatile boolean _evaluated;

		private Lazy(
			final T value,
			final boolean evaluated,
			final Supplier<T> supplier
		) {
			_value = value;
			_evaluated = evaluated;
			_supplier = supplier;
		}

		private Lazy(final Supplier<T> supplier) {
			this(null, false, requireNonNull(supplier));
		}

		@Override
		public T get() throws SQLException {
			return _evaluated ? _value : evaluate();
		}

		/**
		 * Return the evaluation state of the {@code Lazy} variable.
		 *
		 * @return {@code true} is the {@code Lazy} variable has been evaluated,
		 *         {@code false} otherwise
		 */
		public synchronized boolean isEvaluated() {
			return _evaluated;
		}

		private synchronized T evaluate() throws SQLException {
			if (!_evaluated) {
				_value = _supplier.get();
				_evaluated = true;
			}

			return _value;
		}

		/**
		 * Create a new lazy value initialization.
		 *
		 * @param supplier the lazy value supplier
		 * @param <T> the value type
		 * @return a new lazy value initialization
		 * @throws java.lang.NullPointerException if the given supplier is
		 *         {@code null}
		 */
		public static <T> Lazy<T> of(final Supplier<T> supplier) {
			return new Lazy<>(supplier);
		}

		/**
		 * Create a new {@code Lazy} object with the given {@code value}. This
		 * method allows to create a <em>lazy</em> object with the given
		 * {@code value}.
		 *
		 * @since 3.7
		 *
		 * @param value the value this {@code Lazy} object is initialized with
		 * @param <T> the value type
		 * @return return a new lazy value with the given value
		 */
		public static <T> Lazy<T> ofValue(final T value) {
			return new Lazy<T>(value, true, null);
		}

	}

}
