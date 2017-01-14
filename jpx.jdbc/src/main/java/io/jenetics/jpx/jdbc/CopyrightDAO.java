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

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CopyrightDAO
	extends DAO
	implements
		SelectBy<Copyright>,
		Insert<Copyright>,
		Update<Copyright>,
	Delete
{

	public CopyrightDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Copyright} object from a given
	 * DB row.
	 */
	private static final RowParser<Stored<Copyright>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Copyright.of(
			rs.getString("author"),
			rs.getYear("year"),
			rs.getURI("license")
		)
	);

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available copyrights.
	 *
	 * @return all stored copyrights
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Copyright>> select() throws SQLException {
		final String query =
			"SELECT id, author, year, license " +
			"FROM copyright " +
			"ORDER BY id";

		return SQL(query).as(RowParser.list());
	}

	@Override
	public <V, C> List<Stored<Copyright>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final String query =
			"SELECT id, author, year, license " +
			"FROM copyright " +
			"WHERE "+column.name()+" IN ({values}) " +
			"ORDER BY id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}

	public List<Stored<Copyright>> select(final Collection<Copyright> copyrights)
		throws SQLException
	{
		final String query =
			"SELECT id, author, year, license " +
			"FROM copyright " +
			"WHERE author = {author} AND " +
				"year = {year} AND " +
				"license = {license} " +
			"ORDER BY id";

		return Batch(query).select(copyrights, copyright -> asList(
				Param.value("author", copyright.getAuthor()),
				Param.value("year", copyright.getYear()),
				Param.value("license", copyright.getLicense())
			))
			.as(RowParser.list());
	}


	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given copyright list into the DB.
	 *
	 * @param copyrights the links to insert
	 * @return return the stored copyrights
	 * @throws SQLException if inserting fails
	 */
	@Override
	public List<Stored<Copyright>> insert(final Collection<Copyright> copyrights)
		throws SQLException
	{
		final String query =
			"INSERT INTO copyright(author, year, license) " +
			"VALUES({author}, {year}, {license})";

		return Batch(query).insert(copyrights, copyright -> asList(
			Param.value("author", copyright.getAuthor()),
			Param.value("year", copyright.getYear()),
			Param.value("license", copyright.getLicense())
		));
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	/**
	 * Updates the given list of already inserted copyright objects.
	 *
	 * @param copyrights the copyrights to update
	 * @return the updated copyrights
	 * @throws SQLException if the update fails
	 */
	@Override
	public List<Stored<Copyright>> update(
		final Collection<Stored<Copyright>> copyrights
	)
		throws SQLException
	{
		final String query =
			"UPDATE copyright " +
			"SET author = {author}, " +
				"year = {year}, " +
				"license = {license} " +
			"WHERE id = {id}";

		Batch(query).update(copyrights, copyright -> asList(
			Param.value("id", copyright.id()),
			Param.value("author", copyright.value().getAuthor()),
			Param.value("year", copyright.value().getYear()),
			Param.value("license", copyright.value().getLicense())
		));

		return new ArrayList<>(copyrights);
	}

	/**
	 * Inserts the given copyrights into the DB.
	 *
	 * @param copyrights the links to insert or update
	 * @return the inserted or updated links
	 * @throws SQLException if the insert/update fails
	 */
	public List<Stored<Copyright>> put(final Collection<Copyright> copyrights)
		throws SQLException
	{
		return DAO.put(
				copyrights,
				copyright -> copyright,
				this::select,
				this::insert,
				this::update
			);

	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	@Override
	public <V, C> int deleteByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final int count;
		if (!values.isEmpty()) {
			final String query =
				"DELETE FROM copyright WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}

}
