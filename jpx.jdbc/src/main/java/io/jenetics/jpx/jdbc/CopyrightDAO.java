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
import static java.util.Collections.singletonList;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Link;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CopyrightDAO extends DAO {

	public CopyrightDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Copyright>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Copyright.of(
			rs.getString("author"),
			toYear(rs.getInt("year")),
			toURI(rs.getString("license"))
		)
	);

	private static Year toYear(final int year) {
		return year != 0 ? Year.of(year) : null;
	}

	private static URI toURI(final String string) {
		URI uri = null;
		if (string != null) {
			try { uri = new URI(string); } catch (URISyntaxException ignore) {}
		}

		return uri;
	}

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
			"SELECT id, author, year, license FROM copyright;";

		return SQL(query).as(RowParser.list());
	}

	/**
	 * Selects the copyright by its authors
	 *
	 * @param copyrights the author list
	 * @return the copyrights with the given authors currently in the DB
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Copyright>> select(final Collection<Copyright> copyrights)
		throws SQLException
	{
		final String query =
			"SELECT id, author, year, license " +
			"FROM copyright " +
			"WHERE author IN ({authors});";

		return SQL(query)
			.on(Param.values("authors", copyrights, Copyright::getAuthor))
			.as(RowParser.list());
	}

	public List<Stored<Copyright>> selectByID(final Collection<Long> ids)
		throws SQLException
	{
		final String query =
			"SELECT id, author, year, license " +
			"FROM copyright " +
			"WHERE id IN ({ids});";

		return SQL(query).on(Param.values("ids", ids)).as(RowParser.list());
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

	/**
	 * Insert the given copyright into the DB.
	 *
	 * @param copyright the copyright to insert
	 * @return return the stored copyright
	 * @throws SQLException if inserting fails
	 */
	public Stored<Copyright> insert(final Copyright copyright)
		throws SQLException
	{
		return insert(singletonList(copyright)).get(0);
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
	public List<Stored<Copyright>> update(final Collection<Stored<Copyright>> copyrights)
		throws SQLException
	{
		final String query =
			"UPDATE copyright SET year = {year}, license = {license} " +
			"WHERE id = {id}";

		Batch(query).update(copyrights, copyright -> asList(
			Param.value("id", copyright.id()),
			Param.value("year", copyright.value().getYear()),
			Param.value("license", copyright.value().getLicense())
		));

		return new ArrayList<>(copyrights);
	}

	/**
	 * Update the given copyright.
	 *
	 * @param copyright the copyright to update
	 * @return the updated copyright
	 * @throws SQLException if the update fails
	 */
	public Stored<Copyright> update(final Stored<Copyright> copyright)
		throws SQLException
	{
		return update(singletonList(copyright)).get(0);
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
		return copyrights.isEmpty()
			? Collections.emptyList()
			: DAO.put(
				copyrights,
				Copyright::getAuthor,
				this::select,
				this::insert,
				this::update
			);
	}

}
