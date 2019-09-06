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
package io.jenetics.jpx.jdbc.internal.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.Link;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class RouteAccess {
	private RouteAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO route(" +
			"name, " +
			"cmt, " +
			"dscr, " +
			"src, " +
			"number, " +
			"type" +
		") " +
		"VALUES(" +
			"{name}, " +
			"{cmt}, " +
			"{dscr}, " +
			"{src}, " +
			"{number}, " +
			"{type}" +
		")"
	);

	private static final Dctor<Route> DCTOR = Dctor.of(
		Field.of("name", Route::getName),
		Field.of("cmt", Route::getComment),
		Field.of("dscr", Route::getDescription),
		Field.of("src", Route::getSource),
		Field.of("number", r -> r.getNumber().map(UInt::intValue)),
		Field.of("type", Route::getType)
	);

	public static Long insert(final Route route, final Connection conn)
		throws SQLException
	{
		if (route == null || route.isEmpty()) return null;

		final Long id = INSERT_QUERY.insert(route, DCTOR, conn);
		insertLinks(id, route.getLinks(), conn);
		insertWayPoints(id, route.getPoints(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of(
		"INSERT INTO route_link(route_id, link_id " +
		"VALUES({route_id}, {link_id});"
	);

	private static void insertLinks(
		final Long id,
		final List<Link> links,
		final Connection conn
	)
		throws SQLException
	{
		LINK_INSERT_QUERY.executeInserts(
			links,
			Dctor.of(
				Field.ofValue("route_id", id),
				Field.of("link_id", LinkAccess::insert)
			),
			conn
		);
	}

	private static final Query WAY_POINT_INSERT_QUERY = Query.of(
		"INSERT INTO route_way_point(route_id, way_point_id " +
		"VALUES({route_id}, {way_point_id});"
	);

	private static void insertWayPoints(
		final Long id,
		final List<WayPoint> points,
		final Connection conn
	)
		throws SQLException
	{
		WAY_POINT_INSERT_QUERY.executeInserts(
			points,
			Dctor.of(
				Field.ofValue("route_id", id),
				Field.of("way_point_id", WayPointAccess::insert)
			),
			conn
		);
	}

}
