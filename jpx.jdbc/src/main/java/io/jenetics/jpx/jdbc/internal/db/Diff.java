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
package io.jenetics.jpx.jdbc.internal.db;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

/**
 * Helper class used for calculating the to <i>remove</i> and <i>add</i>,
 * when the existing and actual elements are given. The diff is calculated
 * according the given map keys.
 *
 * <pre>
 * existing:     |-------------|
 * actual:                 |-----------|
 * intersection:           |---|
 * removed:      |---------|
 * added:                      |--------|
 * </pre>
 *
 * @param <K> the key type which is used for calculating the `Diff`
 * @param <E> the type of the existing objects. Objects from this type are
 *         removed (from the DB)
 * @param <A> the type of the actual objects. Objects from this type are added
 *         (to the DB).
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Diff<K, E, A> {

	private final Map<K, E> _existing;
	private final Map<K, A> _actual;

	private final Set<K> _intersection;

	/**
	 * Create a new {@code Diff} object with the given existing and actual values.
	 *
	 * @param existing the existing elements (records in DB)
	 * @param actual the actual elements (records which should be in the DB after
	 *        the DB update)
	 */
	private Diff(final Map<K, E> existing, final Map<K, A> actual) {
		_existing = requireNonNull(existing);
		_actual = requireNonNull(actual);

		_intersection = new HashSet<>(_existing.keySet());
		_intersection.retainAll(_actual.keySet());
	}

	public List<A> missing() {
		return _actual.entrySet().stream()
			.filter(entry -> !_intersection.contains(entry.getKey()))
			.map(Map.Entry::getValue)
			.collect(toList());
	}

	public List<E> removed() {
		return _existing.entrySet().stream()
			.filter(entry -> !_intersection.contains(entry.getKey()))
			.map(Map.Entry::getValue)
			.collect(toList());
	}

	public Map<E, A> updated(final BiPredicate<E, A> equals) {
		return _intersection.stream()
			.filter(key -> !equals.test(_existing.get(key), _actual.get(key)))
			.collect(toMap(_existing::get, _actual::get, (a, b) -> b));
	}

	public List<E> unchanged(final BiPredicate<E, A> equals) {
		return _intersection.stream()
			.filter(key -> equals.test(_existing.get(key), _actual.get(key)))
			.map(_existing::get)
			.collect(toList());
	}

	/**
	 * Create a new {@code Diff} object with the given existing and actual values.
	 *
	 * @param existing the existing elements (records in DB)
	 * @param actual the actual elements (records which should be in the DB after
	 *        the DB update)
	 * @return a new diff object
	 */
	public static <K, E, A> Diff<K, E, A> of(
		final Map<K, E> existing,
		final Map<K, A> actual
	) {
		return new Diff<>(existing, actual);
	}

}
