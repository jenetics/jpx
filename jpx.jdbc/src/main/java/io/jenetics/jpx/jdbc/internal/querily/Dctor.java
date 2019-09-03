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
package io.jenetics.jpx.jdbc.internal.querily;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class represents a <em>deconstructor</em> for a given (record) class. It
 * allows to extract the fields, inclusively names, from a given record.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Dctor<T> implements BiFunction<T, String, Value> {

	/**
	 * Deconstructed field from a record class of type {@code T}.
	 *
	 * @param <T> the record type this field belongs to
	 */
	public static final class Field<T, R> {
		private final String _name;
		private final Function<? super T, ? extends R> _accessor;

		private Field(
			final String name,
			final Function<? super T, ? extends R> accessor
		) {
			_name = requireNonNull(name);
			_accessor = requireNonNull(accessor);
		}

		/**
		 * Return the name of the record field.
		 *
		 * @return the field name
		 */
		public String name() {
			return _name;
		}

		/**
		 * The field accessor for the record type {@code T}.
		 *
		 * @return the record field accessor
		 */
		public Function<? super T, ? extends R> accessor() {
			return _accessor;
		}

		/**
		 * Return the field value from the given {@code record} instance.
		 *
		 * @param record the record from where to fetch the field value
		 * @return the record field value
		 */
		public R value(final T record) {
			return _accessor.apply(record);
		}

		/**
		 * Create a new record field with the given {@code name} and field
		 * {@code accessor}.
		 *
		 * @param name the field name
		 * @param accessor the field accessor
		 * @param <T> the record type
		 * @param <R> the field type
		 * @return a new record field
		 */
		public static <T, R> Field<T, R> of(
			final String name,
			final Function<? super T, ? extends R> accessor
		) {
			return new Field<>(name, accessor);
		}
	}

	private final List<Field<T, ?>> _fields;

	private Dctor(final List<Field<T, ?>> fields) {
		_fields = unmodifiableList(fields);
	}

	public List<Field<T, ?>> fields() {
		return _fields;
	}

	public Map<String, Object> deconstruct(final T record) {
		final Map<String, Object> fields = new HashMap<>();
		for (Field<T, ?> field : _fields) {
			fields.put(field.name(), field.value(record));
		}
		return fields;
	}

	@Override
	public Value apply(final T record, final String name) {
		return _fields.stream()
			.filter(f -> Objects.equals(f.name(), name))
			.findFirst()
			.map(f -> Value.of(f.value(record)))
			.orElse(null);
	}

	@SafeVarargs
	public static <T> Dctor<T> of(final Field<T, ?>... fields) {
		return new Dctor<>(asList(fields));
	}

	public static <T> Dctor<T> of(final List<Field<T, ?>> fields) {
		return new Dctor<>(new ArrayList<>(fields));
	}

}
