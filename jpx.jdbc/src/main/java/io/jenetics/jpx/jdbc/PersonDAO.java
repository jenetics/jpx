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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.db.Column;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class PersonDAO
	extends DAO
	implements
		SelectBy<Person>,
		Insert<Person>,
		Update<Person>,
	Delete
{

	public PersonDAO(final Connection conn) {
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
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available persons.
	 *
	 * @return all available stored persons
	 * @throws SQLException if the operation fails
	 */
	public List<Stored<Person>> select() throws SQLException {
		final String query =
			"SELECT person.id, " +
				"name, " +
				"email, " +
				"link.href AS link_href, " +
				"link.text AS link_text, " +
				"link.type AS link_type " +
				"FROM person " +
				"LEFT OUTER JOIN link ON (person.link_id = link.id) " +
				"ORDER BY person.id";

		return SQL(query).as(RowParser.list());
	}

	@Override
	public <V, C> List<Stored<Person>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
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
			"LEFT OUTER JOIN link ON (person.link_id = link.id) " +
			"WHERE "+column.name()+" IN ({values}) " +
			"ORDER BY person.id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}


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
	@Override
	public List<Stored<Person>> insert(final Collection<Person> persons)
		throws SQLException
	{
		final Map<Link, Long> links = DAO
			.write(persons, Person::getLink, with(LinkDAO::new)::put);

		final String query =
			"INSERT INTO person(name, email, link_id) " +
			"VALUES({name}, {email}, {link_id});";

		return Batch(query).insert(persons, person -> asList(
			Param.value("name", person.getName()),
			Param.value("email", person.getEmail().map(Email::getAddress)),
			Param.value("link_id", person.getLink().map(links::get))
		));
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
	@Override
	public List<Stored<Person>> update(final Collection<Stored<Person>> persons)
		throws SQLException
	{
		final Map<Link, Long> links = DAO.write(
			persons,
			(Stored<Person> p) -> p.value().getLink(),
			with(LinkDAO::new)::put
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

		return new ArrayList<>(persons);
	}

	/**
	 * Inserts the given persons into the DB. If the DB already contains the
	 * given person, the person is updated.
	 *
	 * @param persons the links to insert or update
	 * @return the inserted or updated links
	 * @throws SQLException if the insert/update fails
	 */
	public List<Stored<Person>> put(final Collection<Person> persons)
		throws SQLException
	{
		return DAO.put(
			persons,
			Person::getName,
			values -> selectByVals(Column.of("name", Person::getName), persons),
			this::insert,
			this::update
		);
	}

	public Stored<Person> put(final Person person) throws SQLException {
		return put(singletonList(person)).get(0);
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
				"DELETE FROM person WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}
}
