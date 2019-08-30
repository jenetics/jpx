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

import static java.util.Collections.singletonList;
import static io.jenetics.jpx.jdbc.internal.util.Lists.map;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * This interface defines insertion methods for a given row type.
 *
 * @param <T> the row type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface Inserter<T> {

	/**
	 * Insert the given objects into the DB.
	 *
	 * @param values the objects to insert
	 * @return return the inserted objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<T>> insert(final Collection<T> values)
		throws SQLException;

	/**
	 *
	 * @param values the objects to insert
	 * @param mapper the object mapper
	 * @param <A> the raw object type
	 * @return return the inserted objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default <A> List<Stored<T>> insert(
		final Collection<A> values,
		final Function<A, T> mapper
	)
		throws SQLException
	{
		return insert(map(values, mapper));
	}

	/**
	 * Insert the given object into the DB.
	 *
	 * @param value the value to insert
	 * @return return the stored value
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default Stored<T> insert(final T value)
		throws SQLException
	{
		final List<Stored<T>> result = insert(singletonList(value));
		return result.get(0);
	}

}
