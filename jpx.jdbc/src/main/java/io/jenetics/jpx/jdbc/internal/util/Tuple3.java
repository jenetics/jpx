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
package io.jenetics.jpx.jdbc.internal.util;

import static java.lang.String.format;

import java.util.Objects;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Tuple3<T1, T2, T3> {

	final T1 _1;
	final T2 _2;
	final T3 _3;

	private Tuple3(final T1 p1, final T2 p2, final T3 p3) {
		_1 = p1;
		_2 = p2;
		_3 = p3;
	}

	T1 _1() {
		return _1;
	}

	T2 _2() {
		return _2;
	}

	T3 _3() {
		return _3;
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_1) + 31;
		hash += 17*Objects.hashCode(_2) + 31;
		hash += 17*Objects.hashCode(_3) + 31;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Tuple3<?, ?, ?> &&
			Objects.equals(((Tuple3)obj)._1, _1) &&
			Objects.equals(((Tuple3)obj)._2, _2) &&
			Objects.equals(((Tuple3)obj)._3, _3);
	}

	@Override
	public String toString() {
		return format("Pair[%s, %s]", _1, _2);
	}

	static <T1, T2, T3> Tuple3<T1, T2, T3> of(final T1 p1, final T2 p2, final T3 p3) {
		return new Tuple3<>(p1, p2, p3);
	}

}
