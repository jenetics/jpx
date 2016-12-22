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

import static java.util.Collections.singletonList;

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

	private final static RowParser<Stored<Link>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Link.of(
			rs.getString("href"),
			rs.getString("text"),
			rs.getString("type")
		)
	);

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
			"INSERT INTO link(href, text, type) VALUES({href}, {text}, {type});";

		return batch(query).insert(links, link -> Arrays.asList(
			Param.of("href", link.getHref().toString()),
			Param.of("text", link.getText()),
			Param.of("type", link.getType())
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

	public List<Stored<Link>> update(final List<Stored<Link>> links)
		throws SQLException
	{
		final String query =
			"UPDATE link SET text = {text}, type = {type} WHERE id = {id}";

		batch(query).update(links, link -> Arrays.asList(
			Param.of("text", link.map(Link::getText)),
			Param.of("type", link.map(Link::getType)),
			Param.of("id", link.getID())
		));

		return links;
	}

	public Stored<Link> update(final Stored<Link> link) throws SQLException {
		return update(singletonList(link)).get(0);
	}

	public Stored<Link> insertOrUpdate(final Link link) throws SQLException {
		return DAO.insertOrUpdate(
			link,
			l -> selectByHRef(l.getHref()),
			this::insert,
			this::update
		);
	}

	/**
	 * Select all available links.
	 *
	 * @return all stored links
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Link>> select() throws SQLException {
		return sql("SELECT id, href, text, type FROM link")
			.as(RowParser.list());
	}

	/**
	 * Select a link by its DB ID.
	 *
	 * @param id the link DB ID
	 * @return the selected link, if available
	 * @throws SQLException if the select fails
	 */
	public Optional<Stored<Link>> selectByID(final long id)
		throws SQLException
	{
		return sql("SELECT id, href, text, type FROM link WHERE id = {id};")
			.on("id", id)
			.as(RowParser.singleOpt());
	}

	/**
	 * Select the links by the HREF.
	 *
	 * @param href the href to select
	 * @return the selected links
	 * @throws SQLException if the select fails
	 */
	public Optional<Stored<Link>> selectByHRef(final URI href)
		throws SQLException
	{
		return sql("SELECT id, href, text, type FROM link WHERE href = {href}")
			.on("href", href.toString())
			.as(RowParser.singleOpt());
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
