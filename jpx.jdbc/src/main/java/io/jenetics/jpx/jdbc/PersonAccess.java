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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc;

import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Param.value;

import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PersonAccess {
	private PersonAccess() {}

	private static final Query SELECT = Query.of(
		"SELECT person.id, name, email, link_href, link_text, link_type " +
		"FROM person " +
		"INNER JOIN link on person.link_id = link.id " +
		"WHERE person.id = :id"
	);

	private static final Query INSERT = Query.of(
		"INSERT INTO person(name, email, link_id) " +
		"VALUES(:name, :email, :link_id);"
	);

	private static final RowParser<Person> PARSER = row -> Person.of(
		row.getString("name"),
		Email.of(row.getString("email")),
		Link.of(
			row.getString("link_href"),
			row.getString("link_text"),
			row.getString("link_type")
		)
	);

	private static final Dctor<Person> DCTOR = Dctor.of(
		field("name", Person::getName),
		field("email", p -> p.getEmail().map(Email::getAddress)),
		field("link_id", (p, c) -> LinkAccess.insert(p.getLink().orElse(null), c))
	);

	public static Person selectById(final Long id, final Connection conn)
		throws SQLException
	{
		return id != null
			? SELECT
				.on(value("id", id))
				.as(PARSER.singleNullable(), conn)
			: null;
	}

	public static Long insert(final Person person, final Connection conn)
		throws SQLException
	{
		return person != null && !person.isEmpty()
			? INSERT
				.on(person, DCTOR)
				.executeInsert(conn)
				.orElseThrow()
			: null;
	}

}
