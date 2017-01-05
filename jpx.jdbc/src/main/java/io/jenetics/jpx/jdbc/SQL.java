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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

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

	/**
	 * A container object which may or may not contain a non-null value.
	 *
	 * @see java.util.Optional
	 *
	 * @param <T> the element type
	 */
	public static final class Option<T> {

		private static final Option<?> EMPTY = new Option<>();

		private final T _value;

		private Option() {
			_value = null;
		}

		private Option(final T value) {
			_value = requireNonNull(value);
		}

		@SuppressWarnings("unchecked")
		public static<T> Option<T> empty() {
			return (Option<T>)EMPTY;
		}

		public static <T> Option<T> of(final T value) {
			return value == null ? empty() : new Option<>(value);
		}

		public Optional<T> toOptional() {
			return Optional.ofNullable(_value);
		}

		public T get() {
			if (_value == null) {
				throw new NoSuchElementException("No value present");
			}
			return _value;
		}

		public boolean isPresent() {
			return _value != null;
		}

		public void ifPresent(final Consumer<? super T> consumer)
			throws SQLException
		{
			if (_value != null) {
				consumer.accept(_value);
			}
		}

		public Option<T> filter(final Predicate<? super T> predicate) {
			requireNonNull(predicate);
			return !isPresent()
				? this
				: predicate.test(_value)
					? this
					: empty();
		}

		public<U> Option<U> map(final Function<? super T, ? extends U> mapper)
			throws SQLException
		{
			requireNonNull(mapper);
			return !isPresent()
				? empty()
				: Option.of(mapper.apply(_value));
		}

		public<U> Option<U> flatMap(final Function<? super T, Option<U>> mapper)
			throws SQLException
		{
			requireNonNull(mapper);
			return !isPresent()
				? empty()
				: requireNonNull(mapper.apply(_value));
		}

		public T orElse(final T other) {
			return _value != null ? _value : other;
		}

		public T orElseGet(final Supplier<? extends T> other)
			throws SQLException
		{
			return _value != null ? _value : other.get();
		}

		public <X extends Throwable> T orElseThrow(
			final java.util.function.Supplier<? extends X> exceptionSupplier
		)
			throws X
		{
			if (_value != null) {
				return _value;
			} else {
				throw exceptionSupplier.get();
			}
		}

		@Override
		public boolean equals(final Object obj) {
			return obj instanceof Option<?> &&
				Objects.equals(((Option)obj)._value, _value);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(_value);
		}

		@Override
		public String toString() {
			return _value != null
				? String.format("Option[%s]", _value)
				: "Option.empty";
		}
	}


	/**
	 * Class for lazy value initialization.
	 *
	 * @param <T> the result type
	 */
	static final class Lazy<T> implements Supplier<T> {
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
		 * @param value the value this {@code Lazy} object is initialized with
		 * @param <T> the value type
		 * @return return a new lazy value with the given value
		 */
		public static <T> Lazy<T> ofValue(final T value) {
			return new Lazy<T>(value, true, null);
		}

	}

}
