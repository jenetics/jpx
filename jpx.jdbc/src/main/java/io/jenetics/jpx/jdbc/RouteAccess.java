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

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Route;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.WayPoint;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static io.jenetics.facilejdbc.Dctor.field;

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
			":name, " +
			":cmt, " +
			":dscr, " +
			":src, " +
			":number, " +
			":type" +
		")"
	);

	private static final Dctor<Route> DCTOR = Dctor.of(
		field("name", Route::getName),
		field("cmt", Route::getComment),
		field("dscr", Route::getDescription),
		field("src", Route::getSource),
		field("number", r -> r.getNumber().map(UInt::getValue)),
		field("type", Route::getType)
	);

	public static Long insert(final Route route, final Connection conn)
		throws SQLException
	{
		if (route == null || route.isEmpty()) return null;

		final Long id = INSERT_QUERY
			.on(route, DCTOR)
			.executeInsert(conn)
			.orElseThrow();

		insertLinks(id, route.getLinks(), conn);
		insertWayPoints(id, route.getPoints(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of(
		"INSERT INTO route_link(route_id, link_id " +
		"VALUES(:route_id, :link_id);"
	);

	private static void insertLinks(
		final Long id,
		final List<Link> links,
		final Connection conn
	)
		throws SQLException
	{
		final Batch batch = Batch.of(
			links,
			Dctor.of(
				field("route_id", r -> id),
				field("link_id", LinkAccess::insert)
			)
		);

		LINK_INSERT_QUERY.executeUpdate(batch, conn);
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
		final Batch batch = Batch.of(
			points,
			Dctor.of(
				field("route_id", r -> id),
				field("way_point_id", WayPointAccess::insert)
			)
		);

		WAY_POINT_INSERT_QUERY.executeUpdate(batch, conn);
	}

}
