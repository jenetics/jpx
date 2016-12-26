/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package io.jenetics.jpx.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PersonDAO extends DAO {

	private PersonDAO(final Connection conn) {
		super(conn);
	}

	private final static RowParser<Stored<Person>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		Person.of(
			rs.getString("name"),
			Email.of(rs.getString("email")),
			rs.getString("link_href") != null
				? Link.of(
						rs.getString("link_href"),
						rs.getString("link_text"),
						rs.getString("link_type")
					)
				: null
		)
	);

	/**
	 * Insert the given link list into the DB.
	 *
	 * @param persons the persons to insert
	 * @return return the stored links
	 * @throws SQLException if inserting fails
	 */
	public List<Stored<Person>> insert(final List<Person> persons)
		throws SQLException
	{
		final String query =
			"INSERT INTO person(name, email, link_id) VALUES({name}, {email}, {link_id});";

		batch(query).insert(persons, person -> Arrays.asList(
			Param.value("name", person.getName()),
			Param.value("email", person.getEmail().map(Email::toString)),
			Param.insert("link_id", () -> SQL.Option.of(person.getLink()).map(l -> dao(LinkDAO::new).insertOrUpdate(l))),
			Param.insert("link_id", () -> dao(LinkDAO::new).insertOrUpdate(person.getLink()))
		));

		return null;
		/*
		return batch(query).insert(links, link -> Arrays.asList(
			Param.of("href", link.getHref().toString()),
			Param.of("text", link.getText().orElse(null)),
			Param.of("type", link.getType().orElse(null))
		));
		*/
	}

	Optional<Stored<Link>> insertOrUpdate(final Optional<Link> link) {
		return Optional.empty();
	}

	/*
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	email VARCHAR(255),
	link_id BIGINT,
	 */

	public static PersonDAO of(final Connection conn) {
		return new PersonDAO(conn);
	}

}
