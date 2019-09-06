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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc;

import java.sql.Connection;
import java.util.Optional;
import java.util.function.Function;

import io.jenetics.jpx.jdbc.internal.querily.SqlFunction2;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Access {
	private Access() {}

	public static <A, T, R> SqlFunction2<Optional<A>, Connection, ? extends R>
	unwrap(
		final Function<Optional<A>, ? extends T> mapper,
		final SqlFunction2<? super T, Connection, ? extends R> f
	) {
		//return (r, c) -> f.apply(r.orElse(null), c);
		return (r, c) -> f.apply(mapper.apply(r), c);
	}

}
