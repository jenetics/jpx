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

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class BoundsDAO
	extends DAO
	implements
		SelectBy<Bounds>,
		Insert<Bounds>,
		Update<Bounds>,
	Delete
{

	public BoundsDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Bounds} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Bounds>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Bounds.of(
			rs.getDouble("minlat"),
			rs.getDouble("minlon"),
			rs.getDouble("maxlat"),
			rs.getDouble("maxlon")
		)
	);


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available bounds.
	 *
	 * @return all stored bounds
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Bounds>> select() throws SQLException {
		final String query =
			"SELECT id, minlat, minlon, maxlat, maxlon FROM bounds ORDER BY id";

		return SQL(query).as(RowParser.list());
	}

	@Override
	public <V, C> List<Stored<Bounds>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final String query =
			"SELECT id, minlat, minlon, maxlat, maxlon " +
			"FROM bounds " +
			"WHERE "+column.name()+" IN({values}) " +
			"ORDER BY id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}


	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given bounds list into the DB.
	 *
	 * @param bounds the bounds to insert
	 * @return return the stored copyrights
	 * @throws SQLException if inserting fails
	 */
	@Override
	public List<Stored<Bounds>> insert(final Collection<Bounds> bounds)
		throws SQLException
	{
		final String query =
			"INSERT INTO bounds(minlat, minlon, maxlat, maxlon) " +
			"VALUES({minlat}, {minlon}, {maxlat}, {maxlon})";

		return Batch(query).insert(bounds, bound -> asList(
			Param.value("minlat", bound.getMinLatitude().doubleValue()),
			Param.value("minlon", bound.getMinLongitude().doubleValue()),
			Param.value("maxlat", bound.getMaxLatitude().doubleValue()),
			Param.value("maxlon", bound.getMaxLongitude().doubleValue())
		));
	}

	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	/**
	 * Updates the given list of already inserted link objects.
	 *
	 * @param bounds the bounds to update
	 * @return the updated bounds
	 * @throws SQLException if the update fails
	 */
	@Override
	public List<Stored<Bounds>> update(final Collection<Stored<Bounds>> bounds)
		throws SQLException
	{
		final String query =
			"UPDATE bounds " +
			"SET minlat = {minlat}, minlon = {minlon}, " +
				"maxlat = {maxlat}, maxlon = {maxlon} " +
			"WHERE id = {id}";

		Batch(query).update(bounds, bound -> asList(
			Param.value("id", bound.id()),
			Param.value("minlat", bound.value().getMinLatitude().doubleValue()),
			Param.value("minlon", bound.value().getMinLongitude().doubleValue()),
			Param.value("maxlat", bound.value().getMaxLatitude().doubleValue()),
			Param.value("maxlon", bound.value().getMaxLongitude().doubleValue())
		));

		return new ArrayList<>(bounds);
	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	@Override
	public <T, C> int deleteByVals(
		final Column<T, C> column,
		final Collection<T> values
	)
		throws SQLException
	{
		final int count;
		if (!values.isEmpty()) {
			final String query =
				"DELETE FROM bounds WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}

}
