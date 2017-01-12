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

import static java.lang.String.format;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a DB `value` with its database ID. Together with the implicit
 * conversion functions, the properties of the underlying `Stored` object can
 * be <em>directly</em> accessed.
 *
 * <pre>{@code
 *     final case class MyDataObject(name: String, score: Double)
 *     val storedDataObject: Stored[MyDataObject] = select(...)
 *
 *     // "Direct" access of the MyDataObject properties (implicit conversion).
 *     println("NAME: " + storedDataObject.name)
 *     println("SCORE: " + storedDataObject.score)
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Stored<T> {

	private final long _id;
	private final T _value;

	/**
	 * Create a new {@code Stored} object with the given <em>id</em> and
	 * <em>value</em>.
	 *
	 * @param id the DB id
	 * @param value the DB value
	 */
	private Stored(final long id, final T value) {
		_id = id;
		_value = value;
	}

	/**
	 * Return the DB id.
	 *
	 * @return the DB id
	 */
	public long id() {
		return _id;
	}

	/**
	 * Return the DB value as {@link Optional}.
	 *
	 * @return the DB value as {@link Optional}
	 */
	public Optional<T> optional() {
		return Optional.ofNullable(_value);
	}

	/**
	 * Return the DB value.
	 *
	 * @return the DB value
	 */
	public T value() {
		return _value;
	}

	@SuppressWarnings("unchecked")
	public <B> Stored<B> map(final Function<T, B> mapper) {
		return _value != null
			? Stored.of(_id, mapper.apply(_value))
			: (Stored<B>)this;
	}

	/**
	 * Return a new stored object with the new value.
	 *
	 * @param value the new value
	 * @return a new stored object
	 */
	public Stored<T> copy(final T value) {
		return of(_id, value);
	}

	@Override
	public int hashCode() {
		int hash = 31;
		hash += 37*Objects.hashCode(_id) + 17;
		hash += 37*Objects.hashCode(_value) + 17;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Stored<?> &&
			Objects.equals(((Stored)obj)._id, _id) &&
			Objects.equals(((Stored)obj)._value, _value);
	}

	@Override
	public String toString() {
		return format("Stored[id=%d, %s]", _id, _value);
	}

	/**
	 * Create a new {@code Stored} object with the given <em>id</em> and
	 * <em>value</em>.
	 *
	 * @param id the DB id
	 * @param value the DB value
	 * @param <T> the stored value type
	 * @return a new stored object
	 */
	public static <T> Stored<T> of(final long id, final T value) {
		return new Stored<T>(id, value);
	}

}
