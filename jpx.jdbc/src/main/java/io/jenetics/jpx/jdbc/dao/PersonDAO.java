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
package io.jenetics.jpx.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;
import io.jenetics.jpx.jdbc.internal.querily.RowParser;
import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PersonDAO {

	private PersonDAO() {
	}

	private final static RowParser<Stored<Person>> ROW_PARSER = rs -> Stored.of(
		rs.getLong("id"),
		Person.of(
			rs.getString("name"),
			Email.of(rs.getString("email")),
			rs.getString("link_href") != null
				? Link.of(
					rs.getString("link_href"),
					rs.getString("link_text"),
					rs.getString("link_type"))
				: null
		)
	);

	public static Long insert(final Person person, final Connection conn)
		throws SQLException
	{
		if (person == null || person.isEmpty()) {
			return null;
		}

		final String sql =
			"INSERT INTO person(name, email, link_id) " +
				"VALUES({name}, {email}, {link_id});";

		return Query.of(sql).executeInsert(
			person,
			Dctor.of(
				Field.of("name", Person::getName),
				Field.of("email", Person::getEmail),
				Field.of("link_id", p -> linkId(p, conn))
			),
			conn
		);
	}

	private static Long linkId(final Person person, final Connection conn)
		throws SQLException
	{
		return LinkDAO.insert(person.getLink().orElse(null), conn);
	}

}
