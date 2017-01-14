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

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import io.jenetics.jpx.jdbc.Stored;
import io.jenetics.jpx.jdbc.internal.db.Column;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface SelectBy<T> {

	/**
	 * Selects the all stored objects with the given column values.
	 *
	 * @param column the column to select
	 * @param values the value list
	 * @param <V> the value type
	 * @param <C> the column type
	 * @return the selected stored object with the given values
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <V, C> List<Stored<T>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	) throws SQLException;

	/**
	 * Selects the all stored objects with the given column value.
	 *
	 * @param column the column to select
	 * @param value the selection value
	 * @param <V> the value type
	 * @param <C> the column type
	 * @return the selected stored objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default <V, C> List<Stored<T>> selectBy(
		final Column<V, C> column,
		final V value
	)
		throws SQLException
	{
		return selectByVals(column, singletonList(value));
	}

	/**
	 * Selects the all stored objects with the given column value.
	 *
	 * @param column the column to select
	 * @param values the selection values
	 * @param <V> the value type
	 * @return the selected stored objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default  <V> List<Stored<T>> selectByVals(
		final String column,
		final Collection<V> values
	)
		throws SQLException
	{
		return selectByVals(Column.<V>of(column), values);
	}

	/**
	 * Selects the all stored objects with the given column value.
	 *
	 * @param column the column to select
	 * @param value the selection value
	 * @param <V> the value type
	 * @return the selected stored objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default  <V> List<Stored<T>> selectBy(
		final String column,
		final V value
	)
		throws SQLException
	{
		return selectBy(Column.of(column), value);
	}

}
