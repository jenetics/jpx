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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
			list -> selectByHrefs(
				list.stream()
					.map(Link::getHref)
					.collect(Collectors.toList())
			),
			this::insert,
			this::update
		);
	}


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

	public List<Stored<Link>> selectByIDs(final long... ids) throws SQLException {
		final String query =
			"SELECT id, href, text, type FROM link WHERE id IN ({ids})";

		return SQL(query)
			.on(Param.values("ids", ids))
			.as(RowParser.list());
	}

	/**
	 * Select a link by its DB ID.
	 *
	 * @param id the link DB ID
	 * @return the selected link, if available
	 * @throws SQLException if the select fails
	 */
	public SQL.Option<Stored<Link>> selectByID(final long id)
		throws SQLException
	{
		final String query =
			"SELECT id, href, text, type FROM link WHERE id = {id};";

		return SQL(query)
			.on("id", id)
			.as(RowParser.singleOpt());
	}

	/**
	 * Selects the links by its hrefs.
	 *
	 * @param hrefs the hrefs
	 * @return the link with the given hrefs currently in the DB
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Link>> selectByHrefs(final List<URI> hrefs)
		throws SQLException
	{
		return SQL("SELECT id, href, text, type FROM link WHERE href IN ({hrefs})")
			.on(Param.values("hrefs", hrefs))
			.as(RowParser.list());
	}

	/**
	 * Create a new {@code LinkDAO} for the given connection.
	 *
	 * @param conn the DB connection
	 * @return a new DAO object
	 */
	public static LinkDAO of(final Connection conn) {
		return new LinkDAO(conn);
	}

}
