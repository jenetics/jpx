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

import io.jenetics.jpx.Bounds;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class BoundsAccess {
	private BoundsAccess() {}

	private static final Query SELECT = Query.of(
		"SELECT minlat, minlon, maxlat, maxlon " +
		"FROM bounds " +
		"WHERE id = :id"
	);

	private static final Query INSERT = Query.of(
		"INSERT INTO bounds(minlat, minlon, maxlat, maxlon) " +
		"VALUES(:minlat, :minlon, :maxlat, :maxlon)"
	);

	private static final RowParser<Bounds> PARSER = (row, conn) -> Bounds.of(
		row.getDouble("minlat"),
		row.getDouble("minlon"),
		row.getDouble("maxlat"),
		row.getDouble("maxlon")
	);

	private static final Dctor<Bounds> DCTOR = Dctor.of(
		field("minlat", Bounds::getMinLatitude),
		field("minlon", Bounds::getMinLongitude),
		field("maxlat", Bounds::getMaxLatitude),
		field("maxlon", Bounds::getMaxLongitude)
	);

	public static Bounds selectById(final Long id, final Connection conn)
		throws SQLException
	{
		return id != null
			? SELECT
				.on(value("id", id))
				.as(PARSER.singleNull(), conn)
			: null;
	}

	public static Long insert(final Bounds bounds, final Connection conn)
		throws SQLException
	{
		return bounds != null
			? INSERT
				.on(bounds, DCTOR)
				.executeInsert(conn)
				.orElseThrow()
			: null;
	}

}
