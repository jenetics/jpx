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

import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;
import io.jenetics.jpx.jdbc.internal.db.Insert;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.SelectBy;
import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * DAO for the {@code Link} data class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkDAO
	extends DAO
	implements
	SelectBy<Link>,
	Insert<Link>,
		Update<Link>,
	Delete
{

	public LinkDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<Link>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Link.of(
			rs.getString("href"),
			rs.getString("text"),
			rs.getString("type")
		)
	);


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available links.
	 *
	 * @return all stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<Link>> select() throws SQLException {
		final String query =
			"SELECT id, href, text, type FROM link ORDER BY id";

		return SQL(query).as(RowParser.list());
	}

	@Override
	public <V, C> List<Stored<Link>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
			final String query =
				"SELECT id, href, text, type " +
				"FROM link WHERE "+column.name()+" IN ({values}) " +
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

	@Override
	public List<Stored<Link>> insert(final Collection<Link> links)
		throws SQLException
	{
		final String query =
			"INSERT INTO link(href, text, type) " +
			"VALUES({href}, {text}, {type});";

		return Batch(query).insert(links, link -> asList(
			Param.value("href", link.getHref()),
			Param.value("text", link.getText()),
			Param.value("type", link.getType())
		));
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	@Override
	public List<Stored<Link>> update(final Collection<Stored<Link>> links)
		throws SQLException
	{
		final String query =
			"UPDATE link SET text = {text}, type = {type} " +
			"WHERE id = {id}";

		Batch(query).update(links, link -> asList(
			Param.value("id", link.id()),
			Param.value("text", link.value().getText()),
			Param.value("type", link.value().getType())
		));

		return new ArrayList<>(links);
	}

	/**
	 * Inserts the given links into the DB. If the DB already contains the given
	 * link, the link is updated.
	 *
	 * @param links the links to insert or update
	 * @return the inserted or updated links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<Link>> put(final Collection<Link> links)
		throws SQLException
	{
		return DAO.put(
			links,
			Link::getHref,
			values -> selectByVals(Column.of("href", Link::getHref), links),
			this::insert,
			this::update
		);
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
				"DELETE FROM link WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}

}
