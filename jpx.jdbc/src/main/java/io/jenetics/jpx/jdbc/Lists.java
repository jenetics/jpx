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

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.jenetics.jpx.jdbc.internal.db.ListMapper;
import io.jenetics.jpx.jdbc.internal.db.OptionMapper;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class Lists {

	private Lists() {
	}

	static <A, B> List<B> map(final Collection<A> values, final Function<A, B> mapper) {
		return values.stream()
			.map(mapper)
			.collect(Collectors.toList());
	}

	static <A, B> List<B> flatMap(
		final Collection<A> values,
		final OptionMapper<A, B> mapper
	) {
		return flatMap(values, mapper.toListMapper());
	}

	static <A, B> List<B> flatMap(
		final Collection<A> values,
		final ListMapper<A, B> mapper
	) {
		return values.stream()
			.flatMap(value -> mapper.apply(value).stream())
			.collect(Collectors.toList());
	}

}
