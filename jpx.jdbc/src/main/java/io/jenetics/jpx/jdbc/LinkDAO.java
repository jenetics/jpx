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
import java.util.List;

import io.jenetics.jpx.Link;

/**
 * Link Data Access Object.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Link>> select() throws SQLException {
		return SQL("SELECT id, href, text, type FROM link")
			.as(RowParser.list());
	}

	public List<Stored<Link>> select(final List<Link> links)
		throws SQLException
	{
		final String query =
			"SELECT id, href, text, type " +
			"FROM link WHERE href IN ({hrefs})";

		return SQL(query)
			.on(Param.values("hrefs", links, Link::getHref))
			.as(RowParser.list());
	}

	public List<Stored<Link>> selectByID(final List<Long> ids)
		throws SQLException
	{
		final String query =
			"SELECT id, href, text, type " +
			"FROM link WHERE id IN ({ids})";

		return SQL(query).on(Param.values("ids", ids)).as(RowParser.list());
	}


	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given link list into the DB.
	 *
	 * @param links the links to insert
	 * @return return the stored links
	 * @throws SQLException if inserting fails
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
	 * @throws SQLException if inserting fails
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
	 * @throws SQLException if the update fails
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
	 * @throws SQLException if the update fails
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
	 * @throws SQLException if the insert/update fails
	 */
	public List<Stored<Link>> put(final List<Link> links) throws SQLException {
		return DAO.put(
			links,
			Link::getHref,
			this::select,
			this::insert,
			this::update
		);
	}


	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	public List<Link> delete(final List<Stored<Link>> links)
		throws SQLException
	{
		return null;
	}

}
