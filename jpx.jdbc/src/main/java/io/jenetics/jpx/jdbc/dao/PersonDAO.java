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

import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.RowParser;
import io.jenetics.jpx.jdbc.internal.db.Selector;
import io.jenetics.jpx.jdbc.model.LinkRow;
import io.jenetics.jpx.jdbc.model.PersonRow;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public class PersonDAO extends DAO implements Selector<PersonRow> {
//
//	public PersonDAO(final Connection conn) {
//		super(conn);
//	}
//
//	private final static RowParser<PersonRow> RowParser = rs -> new PersonRow(
//		rs.getLong("id"),
//		rs.getString("name"),
//		rs.getString("email"),
//		rs.get(Long.class, LinkRow::new, "link_id")
//	);
//
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
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
//
//}
