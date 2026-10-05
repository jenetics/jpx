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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper methods for handling lists. All method handles null values correctly.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 1.0
 */
final class Lists {

	private Lists() {
	}

	static <T> List<T> copyOf(final List<? extends T> list) {
		return list == null ? List.of() : List.copyOf(list);
	}

	static <T> void copyTo(
		final List<? extends T> source,
		final List<? super T> target
	) {
		requireNonNull(target);
		if (source != null) {
			source.forEach(Objects::requireNonNull);
		}

		target.clear();
		if (source != null) {
			target.addAll(source);
		}
	}

	static int hashCode(final List<?> list) {
		return list != null
			? 17*list.stream().mapToInt(Objects::hashCode).sum() + 31
			: 0;
	}

	static boolean equals(final List<?> b, final List<?> a) {
		boolean result = false;
		if (a != null) {
			if (b != null) {
				result = a.size() == b.size();
				if (result) {
					result = a.isEmpty()|| containsAll(a, b);
				}
			}
		} else {
			result = b == null;
		}

		return result;
	}

	private static boolean containsAll(final Collection<?> a, final Collection<?> b) {
		final Map<Object, Integer> counts = new HashMap<>();
		for (final Object element : a) {
			counts.merge(element, 1, Integer::sum);
		}

		for (final Object element : b) {
			final Integer count = counts.get(element);
			if (count == null) {
				return false;
			}
			if (count == 1) {
				counts.remove(element);
			} else {
				counts.put(element, count - 1);
			}
		}

		return true;
	}

}
