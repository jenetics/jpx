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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.jenetics.jpx.Link;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkDAO extends DAO<Link> {

	public LinkDAO(final Connection connection) {
		super(connection);
	}

	@Override
	public RowParser<Link> parser() {
		return rs -> Stored.of(
			rs.getLong("id"),
			Link.of(
				rs.getString("href"),
				rs.getString("text"),
				rs.getString("type")
			)
		);
	}

	public List<Stored<Link>> insert(final List<Link> links) throws SQLException {
		final String query = "INSERT INTO link(href, text, type) VALUES(?, ?, ?);";

		final List<Stored<Link>> results = new ArrayList<>();
		try (PreparedStatement stmt = prepareInsert(query)) {
			for (Link link : links) {
				stmt.setString(1, link.getHref().toString());
				stmt.setString(2, link.getText().orElse(null));
				stmt.setString(3, link.getType().orElse(null));

				stmt.executeUpdate();
				results.add(Stored.of(DAO.id(stmt), link));
			}
		}

		return results;
	}

	public Stored<Link> insert(final Link link)
		throws SQLException
	{
		return insert(Collections.singletonList(link)).get(0);
	}

	public List<Stored<Link>> select() throws SQLException {
		final String query = "SELECT id, href, text, type FROM link";

		try (PreparedStatement stmt = _conn.prepareStatement(query)) {
			try (final ResultSet rs = stmt.executeQuery()) {
				return toList(rs);
			}
		}
	}

	public Optional<Stored<Link>> selectByID(final long id)
		throws SQLException
	{
		final String query = "SELECT id, href, text, type FROM link WHERE id = ?;";

		try (PreparedStatement stmt = _conn.prepareStatement(query)) {
			stmt.setLong(1, id);

			try (final ResultSet rs = stmt.executeQuery()) {
				return firstOption(rs);
			}
		}
	}


	public static LinkDAO of(final Connection conn) {
		return new LinkDAO(conn);
	}

}
