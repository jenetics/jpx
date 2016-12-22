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
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Stored<T> {

	private final long _id;
	private final T _value;

	private Stored(final long id, final T value) {
		_id = id;
		_value = value;
	}

	public long getID() {
		return _id;
	}

	public Optional<T> getValue() {
		return Optional.ofNullable(_value);
	}

	public <B> Optional<B> map(final Function<T, B> mapper) {
		return _value != null
			? Optional.of(mapper.apply(_value))
			: Optional.empty();
	}

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

	public static <T> Stored<T> of(final long id, final T value) {
		return new Stored<T>(id, value);
	}

}
