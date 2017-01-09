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

import static java.lang.String.format;

import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Pair<A, B> {
	final A _1;
	final B _2;

	private Pair(final A a, final B b) {
		_1 = a;
		_2 = b;
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

	public static <A, B> Pair<A, B> of(final A a, final B b) {
		return new Pair<A, B>(a, b);
	}

}
