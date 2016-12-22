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
import java.util.Optional;

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

	public static final class Option<T> {
		private static final Option<?> EMPTY = new Option<>();

		private final T _value;

		private Option() {
			_value = null;
		}

		private Option(final T value) {
			_value = requireNonNull(value);
		}

		public boolean isPresent() {
			return _value != null;
		}

		public<U> Option<U> map(Function<? super T, ? extends U> mapper)
			throws SQLException
		{
			requireNonNull(mapper);
			if (!isPresent())
				return empty();
			else {
				return Option.ofNullable(mapper.apply(_value));
			}
		}

		public<U> Option<U> flatMap(Function<? super T, Option<U>> mapper)
			throws SQLException
		{
			requireNonNull(mapper);
			if (!isPresent())
				return empty();
			else {
				return requireNonNull(mapper.apply(_value));
			}
		}

		public T orElse(T other) {
			return _value != null ? _value : other;
		}

		public static <T> Option<T> of(T value) {
			return new Option<>(value);
		}

		public static <T> Option<T> of(final Optional<T> value) {
			return value.isPresent() ? of(value.get()) : empty();
		}

		public static <T> Option<T> ofNullable(T value) {
			return value == null ? empty() : of(value);
		}

		public static <T> Option<T> empty() {
			@SuppressWarnings("unchecked")
			final Option<T> t = (Option<T>)EMPTY;
			return t;
		}
	}

}
