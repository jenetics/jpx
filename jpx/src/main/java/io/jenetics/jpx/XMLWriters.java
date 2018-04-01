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
package io.jenetics.jpx;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import io.jenetics.jpx.GPX.Version;

/**
 * XMLWriter collection for different GPX versions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.3
 * @since 1.3
 */
final class XMLWriters<T> {

	private final List<Version> _versions = new ArrayList<>();
	private final List<XMLWriter<T>> _writers = new ArrayList<>();


	XMLWriters<T> v00(final XMLWriter<T> writer) {
		requireNonNull(writer);
		_versions.add(null);
		_writers.add(writer);
		return this;
	}

	XMLWriters<T> v10(final XMLWriter<T> writer) {
		requireNonNull(writer);
		_versions.add(Version.V10);
		_writers.add(writer);
		return this;
	}

	XMLWriters<T> v11(final XMLWriter<T> writer) {
		requireNonNull(writer);
		_versions.add(Version.V11);
		_writers.add(writer);
		return this;
	}

	@SuppressWarnings("unchecked")
	XMLWriter<T>[] writers(final Version version) {
		return IntStream.range(0, _versions.size())
			.filter(i -> _versions.get(i) == null || _versions.get(i) == version)
			.mapToObj(_writers::get)
			.toArray(XMLWriter[]::new);
	}

}
