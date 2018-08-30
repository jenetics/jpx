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
package io.jenetics.jpx.format;

import java.util.Optional;

/**
 * Base interface for formatting (converting) a given type to it's string
 * representation.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.4
 * @since 1.4
 */
interface Format<T> {

	/**
	 * Formats the given {@code value} to it's string representation. If it is
	 * not possible to convert the {@code value} to a string,
	 * {@link Optional#empty()} is returned.
	 *
	 * @param value the value which is converted to a string.
	 * @return the converted value, or {@link Optional#empty()} if the format
	 *         fails
	 */
	Optional<String> format(final T value);

}
