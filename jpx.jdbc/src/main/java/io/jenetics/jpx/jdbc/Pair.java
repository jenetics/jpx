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

/**
 * Pair of two objects.
 *
 * @param <A> first type
 * @param <B> second type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Pair<A, B> {

	/**
	 * The fist value.
	 */
	final A _1;

	/**
	 * The second value.
	 */
	final B _2;

	private Pair(final A a, final B b) {
		_1 = a;
		_2 = b;
	}

	/**
	 * Return the first value.
	 *
	 * @return the first value
	 */
	A _1() {
		return _1;
	}

	/**
	 * return the second value.
	 *
	 * @return the second value
	 */
	B _2() {
		return _2;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash += 17*Objects.hashCode(_1) + 31;
		hash += 17*Objects.hashCode(_2) + 31;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Pair<?, ?> &&
			Objects.equals(((Pair)obj)._1, _1) &&
			Objects.equals(((Pair)obj)._2, _2);
	}

	@Override
	public String toString() {
		return format("Pair[%s, %s]", _1, _2);
	}

	/**
	 * Create a new pair with the given values.
	 *
	 * @param a the first value
	 * @param b the second value
	 * @param <A> the first type
	 * @param <B> the second type
	 * @return a new pair with the given values
	 */
	public static <A, B> Pair<A, B> of(final A a, final B b) {
		return new Pair<A, B>(a, b);
	}

}
