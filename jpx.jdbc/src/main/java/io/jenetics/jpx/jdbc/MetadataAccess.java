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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class MetadataAccess {
	private MetadataAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO metadata(" +
			"name, " +
			"dscr, " +
			"time, " +
			"keywords, " +
			"person_id, " +
			"copyright_id, " +
			"bounds_id" +
		") " +
		"VALUES(" +
			"{name}, " +
			"{dscr}, " +
			"{time}, " +
			"{keywords}, " +
			"{person_id}, " +
			"{copyright_id}, " +
			"{bounds_id}" +
		")"
	);

	private static final Dctor<Metadata> DCTOR = Dctor.of(
		Field.of("name", Metadata::getName),
		Field.of("dscr", Metadata::getDescription),
		Field.of("time", Metadata::getTime),
		Field.of("keywords", Metadata::getKeywords),
		Field.of(
			"person_id",
			(m, c) -> PersonAccess.insertOrUpdate(m.getAuthor().orElse(null), c)
		),
		Field.of(
			"copyright_id",
			(m, c) -> CopyrightAccess.insert(m.getCopyright().orElse(null), c)
		),
		Field.of(
			"bounds_id",
			(m, c) -> BoundsAccess.insert(m.getBounds().orElse(null), c)
		)
	);

	public static Long insert(final Metadata metadata, final Connection conn)
		throws SQLException
	{
		if (metadata == null || metadata.isEmpty()) return null;

		final Long id = INSERT_QUERY.insert(metadata, DCTOR, conn);
		insertLinks(id, metadata.getLinks(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of(
		"INSERT INTO metadata_link(metadata_id, link_id " +
		"VALUES({metadata_id}, {link_id});"
	);

	private static void insertLinks(
		final Long id,
		final List<Link> links,
		final Connection conn
	)
		throws SQLException
	{
		LINK_INSERT_QUERY.inserts(
			links,
			Dctor.of(
				Field.ofValue("metadata_id", id),
				Field.of("link_id", LinkAccess::insert)
			),
			conn
		);
	}


}
