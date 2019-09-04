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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.jpx.Email;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.querily.Param;
import io.jenetics.jpx.jdbc.internal.querily.Param.Value;
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

	public static List<Long> insert(
		final List<Person> persons,
		final Connection conn
	)
		throws SQLException
	{
		final String sql =
			"INSERT INTO person(name, email, link_id) " +
				"VALUES({name}, {email}, {link_id});";

		final Query query = Query.of(sql);

		final List<Long> ids = new ArrayList<>();
		for (Person person : persons) {
			if (!person.isEmpty()) {
				final Link link = person.getLink().orElse(null);
				final Long lid = link != null
					? LinkDAO.insert(link, conn)
					: null;

				final Long id = query.on(
					Param.of("name", Value.of(person.getName().orElse(null))),
					Param.of("email", Value.of(person.getEmail().orElse(null))),
					Param.of("link_id", Value.of(lid)))
					.executeInsert(conn).orElse(null);

				ids.add(id);
			} else {
				ids.add(null);
			}

		}

		return ids;
	}
	/*
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255),
	link_id BIGINT REFERENCES link(id),
	 */


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

//	/**
//	 * Select all available persons.
//	 *
//	 * @return all available stored persons
//	 * @throws SQLException if the operation fails
//	 */
//	public List<PersonRow> select() throws SQLException {
//		final String query =
//			"SELECT id, " +
//				"name, " +
//				"email, " +
//				"link_id, " +
//			"FROM person " +
//			"ORDER BY id";
//
//		final List<PersonRow> rows = SQL(query).as(RowParser.list());
//		with(LinkDAO::new).fill(rows, p -> p.link);
//
//		return rows;
//	}
//
//	public <T> void fill(
//		final Collection<T> rows,
//		final Function<T, PersonRow> mapper
//	)
//		throws SQLException
//	{
//		final Column<T, Long> col = Column.of("id", row -> mapper.apply(row).id);
//
//		final Map<Long, PersonRow> links = select(col, rows).stream()
//			.collect(toMap(l -> l.id, l -> l, (a, b) -> b));
//
//		/*
//		rows.stream()
//			.map(mapper)
//			.forEach(row -> links.get(row.id).copyTo(row));
//			*/
//	}
//
//	@Override
//	public <V, C> List<PersonRow> select(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		final String query =
//			"SELECT id, " +
//				"name, " +
//				"email, " +
//				"link_id, " +
//			"FROM person " +
//			"WHERE "+column.name()+" IN ({values}) " +
//			"ORDER BY id";
//
//		return values.isEmpty()
//			? Collections.emptyList()
//			: SQL(query)
//			.on(Param.values("values", values, column.mapper()))
//			.as(RowParser.list());
//	}

}
