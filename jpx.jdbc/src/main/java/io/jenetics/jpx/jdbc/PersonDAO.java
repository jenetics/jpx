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
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
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
			rs.getString("email") != null
				? Email.of(rs.getString("email"))
				: null,
			rs.getString("link_href") != null
				? Link.of(
						rs.getString("link_href"),
						rs.getString("link_text"),
						rs.getString("link_type")
					)
				: null
		)
	);


	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given person list into the DB.
	 *
	 * @param persons the persons to insert
	 * @return return the stored persons
	 * @throws SQLException if inserting fails
	 */
	public List<Stored<Person>> insert(final List<Person> persons)
		throws SQLException
	{
		final Map<Link, Long> links = putLinks(persons);

		final String query =
			"INSERT INTO person(name, email, link_id) " +
			"VALUES({name}, {email}, {link_id});";

		return Batch(query).insert(persons, person -> asList(
			Param.value("name", person.getName()),
			Param.value("email", person.getEmail().map(Email::getAddress)),
			Param.value("link_id", person.getLink().map(links::get))
		));
	}

	private Map<Link, Long> putLinks(final List<Person> persons)
		throws SQLException
	{
		final List<Stored<Link>> links = dao(LinkDAO::new).put(
			persons.stream()
				.flatMap(p -> p.getLink().map(Stream::of).orElse(Stream.empty()))
				.collect(Collectors.toList())
		);

		return links.stream()
			.collect(toMap(Stored::value, Stored::id, (a, b) -> a));
	}

	/**
	 * Insert the given person into the DB.
	 *
	 * @param person the person to insert
	 * @return return the stored person
	 * @throws SQLException if inserting fails
	 */
	public Stored<Person> insert(final Person person) throws SQLException {
		return insert(singletonList(person)).get(0);
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	/**
	 * Updates the given list of already inserted link objects.
	 *
	 * @param persons the persons to update
	 * @return the updated persons
	 * @throws SQLException if the update fails
	 */
	public List<Stored<Person>> update(final List<Stored<Person>> persons)
		throws SQLException
	{
		final Map<Link, Long> links = putLinks(
			persons.stream()
				.map(Stored::value)
				.collect(Collectors.toList())
		);

		final String query =
			"UPDATE person " +
			"SET name = {name}, email = {email}, link_id = {link_id} " +
			"WHERE id = {id}";

		Batch(query).update(persons, person -> asList(
			Param.value("id", person.id()),
			Param.value("name", person.value().getName()),
			Param.value("email", person.value().getEmail().map(Email::getAddress)),
			Param.value("link_id", person.value().getLink().map(links::get))
		));

		return persons;
	}

	/**
	 * Update the given person.
	 *
	 * @param person the person to update
	 * @return the updated person
	 * @throws SQLException if the update fails
	 */
	public Stored<Person> update(final Stored<Person> person)
		throws SQLException
	{
		return update(singletonList(person)).get(0);
	}

	/**
	 * Inserts the given persons into the DB. If the DB already contains the
	 * given person, the person is updated.
	 *
	 * @param persons the links to insert or update
	 * @return the inserted or updated links
	 * @throws SQLException if the insert/update fails
	 */
	public List<Stored<Person>> put(final List<Person> persons)
		throws SQLException
	{
		return DAO.put(
			persons,
			Person::getName,
			list -> selectByNames(
				list.stream()
					.flatMap(p -> p.getName()
						.map(Stream::of).orElse(Stream.empty()))
					.collect(Collectors.toList())
			),
			this::insert,
			this::update
		);
	}

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public List<Stored<Person>> select() throws SQLException {
		final String query =
			"SELECT person.id, " +
				"name, " +
				"email, " +
				"link.href AS link_href, " +
				"link.text AS link_text, " +
				"link.type AS link_type " +
			"FROM person " +
			"LEFT OUTER JOIN link ON (person.link_id = link.id)";

		return SQL(query).as(RowParser.list());
	}

	/**
	 * Selects the links by its hrefs.
	 *
	 * @param names the person names
	 * @return the link with the given hrefs currently in the DB
	 * @throws SQLException if the select fails
	 */
	public List<Stored<Person>> selectByNames(final List<String> names)
		throws SQLException
	{
		final String query =
			"SELECT person.id, " +
				"name, " +
				"email, " +
				"link.href AS link_href, " +
				"link.text AS link_text, " +
				"link.type AS link_type " +
			"FROM person " +
			"LEFT OUTER JOIN link ON (person.link_id = link.id)" +
			"WHERE name IN ({names})";

		return SQL(query)
			.on(Param.values("names", names))
			.as(RowParser.list());
	}

	public static PersonDAO of(final Connection conn) {
		return new PersonDAO(conn);
	}

}
