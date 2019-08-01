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

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Filter interface which contains the {@code filter}, {@code map},
 * {@code flatMap} and {@code listMap} methods for transforming values from
 * type {@code T}.
 *
 * @param <T> the value type for the transformed objects
 * @param <R> the result type of the filtered object
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.1
 * @since 1.1
 */
public interface Filter<T, R> {

	/**
	 * Return a filter consisting of the elements of this filter that matches
	 * the given predicate.
	 *
	 * @param predicate a non-interfering, stateless predicate to apply to each
	 *        element to determine if it should be included
	 * @return a new filter
	 */
	public Filter<T, R> filter(final Predicate<? super T> predicate);

	/**
	 * Return a filter with the results of applying the given mapper function.
	 *
	 * @param mapper a non-interfering, stateless function to apply to each
	 *        element
	 * @return a new filter
	 */
	public Filter<T, R> map(final Function<? super T, ? extends T> mapper);

	/**
	 * Return a filter consisting of the results of replacing each element with
	 * the contents of the mapped elements.
	 *
	 * @param mapper a non-interfering, stateless function to apply to each
	 *        element which produces a list of new values
	 * @return a new filter
	 */
	public Filter<T, R>
	flatMap(final Function<? super T, ? extends List<T>> mapper);

	/**
	 * Return a filter with the results of the applying given mapper function.
	 *
	 * @param mapper a non-interfering, stateless function to apply to the
	 *        existing elements
	 * @return a new filter
	 */
	public Filter<T, R>
	listMap(final Function<? super List<T>, ? extends List<T>> mapper);

	/**
	 * Return a new object of type {@code R} which contains the elements of the
	 * applied filter functions.
	 *
	 * @return a new object created from the given filter
	 */
	public R build();


	public static <T> Function<List<T>, List<T>> listMaps(final BinaryOperator<T> op) {
		return null;
	}

}
