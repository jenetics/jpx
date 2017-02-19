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
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Filter<T, R> {

	public Filter<T, R> filter(final Predicate<? super T> predicate);

	public Filter<T, R> map(final Function<? super T, ? extends T> mapper);

	public Filter<T, R> flatMap(final Function<? super T, ? extends List<T>> mapper);

	public Filter<T, R> listMap(final Function<? super List<T>, ? extends List<T>> mapper);

	public R build();

}
