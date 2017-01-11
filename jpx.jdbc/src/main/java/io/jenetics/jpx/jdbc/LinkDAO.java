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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import io.jenetics.jpx.Link;

/**
 * DAO for the {@code Link} data class.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkDAO extends DAO {

	public LinkDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Link>> RowParser = rs -> Stored.of(
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
		return SQL("SELECT id, href, text, type FROM link")
			.as(RowParser.list());
	}

	/**
	 * Selects the all stored link objects with the given column values.
	 *
	 * @param column the column to select
	 * @param values the value list
	 * @return the selected stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <T, C> List<Stored<Link>> selectBy(
		final Column<T, C> column,
		final List<T> values
	)
		throws SQLException
	{
		final List<Stored<Link>> links;
		if (!values.isEmpty()) {
			final String query =
				"SELECT id, href, text, type " +
				"FROM link WHERE "+column.name()+" IN ({values})";

			links = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
		} else {
			links = Collections.emptyList();
		}

		return links;
	}

	/**
	 * Selects the all stored link objects with the given column value.
	 *
	 * @param column the column to select
	 * @param value the selection value
	 * @return the selected stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <T, C> List<Stored<Link>> selectBy(
		final Column<T, C> column,
		final T value
	)
		throws SQLException
	{
		return selectBy(column, singletonList(value));
	}

	/**
	 * Selects the all stored link objects with the given column value.
	 *
	 * @param column the column to select
	 * @param value the selection value
	 * @return the selected stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <T> List<Stored<Link>> selectBy(final String column, final T value)
		throws SQLException
	{
		return selectBy(Column.of(column), value);
	}


	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given link list into the DB.
	 *
	 * @param links the links to insert
	 * @return return the stored links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<Link>> insert(final List<Link> links)
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

	/**
	 * Insert the given link into the DB.
	 *
	 * @param link the link to insert
	 * @return return the stored link
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Stored<Link> insert(final Link link)
		throws SQLException
	{
		return insert(singletonList(link)).get(0);
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	/**
	 * Updates the given list of already inserted link objects.
	 *
	 * @param links the links to update
	 * @return the updated links
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<Link>> update(final List<Stored<Link>> links)
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

		return links;
	}

	/**
	 * Update the given link.
	 *
	 * @param link the link to update
	 * @return the updated link
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public Stored<Link> update(final Stored<Link> link) throws SQLException {
		return update(singletonList(link)).get(0);
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
	public List<Stored<Link>> put(final List<Link> links) throws SQLException {
		return links.isEmpty()
			? Collections.emptyList()
			: DAO.put(
				links,
				Link::getHref,
				values -> selectBy(Column.of("href", Link::getHref), links),
				this::insert,
				this::update
			);
	}


	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	/**
	 * Delete links by the given column values.
	 *
	 * @param column the column which specifies the deleted rows
	 * @param values the rows to delete
	 * @param <T> the value type
	 * @param <C> the column type
	 * @return the number of deleted rows
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public <T, C> int deleteBy(
		final Column<T, C> column,
		final List<T> values
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
