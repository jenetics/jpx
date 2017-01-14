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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;
import io.jenetics.jpx.jdbc.internal.db.Insert;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MetadataLinkDAO
	extends DAO
	implements
	Insert<MetadataLink>,
	Delete
{
	public MetadataLinkDAO(final Connection conn) {
		super(conn);
	}

	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<MetadataLink>> RowParser = rs -> Stored.of(
		rs.getLong("metadata_id"),
		MetadataLink.of(
			rs.getLong("metadata_id"),
			rs.getLong("link_id")
		)
	);

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public <T> Map<Long, List<Link>> selectLinks(
		final Collection<T> values,
		final Function<T, Long> mapper
	)
		throws SQLException
	{
		final String query =
			"SELECT metadata_id, link_id " +
			"FROM metadata_link " +
			"WHERE metadata_id IN ({ids}) " +
			"ORDER BY link_id";

		if (!values.isEmpty()) {
			final List<Stored<MetadataLink>> rows = SQL(query)
				.on(Param.values("ids", values, mapper))
				.as(RowParser.list());

			final Map<Long, Link> links = with(LinkDAO::new)
				.selectByVals(Column.of("id", row -> row.value().getLinkID()), rows)
				.stream()
				.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

			return rows.stream()
				.collect(groupingBy(
					Stored::id,
					mapping(row -> links.get(row.value().getLinkID()), toList())));
		} else {
			return Collections.emptyMap();
		}
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	@Override
	public List<Stored<MetadataLink>> insert(final Collection<MetadataLink> rows)
		throws SQLException
	{
		final String query =
			"INSERT INTO metadata_link(metadata_id, link_id) " +
			"VALUES({metadata_id}, {link_id});";

		Batch(query).execute(rows, row -> asList(
			Param.value("metadata_id", row.getMetadataID()),
			Param.value("link_id", row.getLinkID())
		));

		return map(rows, row ->
			Stored.of(row.getMetadataID(), row));
	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	@Override
	public <V, C> int deleteByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final int count;
		if (!values.isEmpty()) {
			final String query =
				"DELETE FROM metadata_link WHERE "+column.name()+" IN ({values})";

			count = SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.execute();

		} else {
			count = 0;
		}

		return count;
	}

}
