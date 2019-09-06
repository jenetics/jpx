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
import java.time.Duration;
import java.util.List;

import io.jenetics.jpx.DGPSStation;
import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Fix;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Speed;
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
public final class WayPointAccess {
	private WayPointAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO way_point(" +
			"lat, " +
			"lon, " +
			"ele, " +
			"speed, " +
			"time, " +
			"magvar, " +
			"geoidheight, " +
			"name, " +
			"cmt, " +
			"desc, " +
			"src," +
			"sym, " +
			"type, " +
			"fix, " +
			"sat, " +
			"hdop, " +
			"vdop, " +
			"pdop, " +
			"ageofdgpsdata, " +
			"dgpsid " +
			"course " +
		")" +
		"VALUES(" +
			"{lat}, " +
			"{lon}, " +
			"{ele}, " +
			"{speed}, " +
			"{time}, " +
			"{magvar}, " +
			"{geoidheight}, " +
			"{name}, " +
			"{cmt}, " +
			"{desc}, " +
			"{src}," +
			"{sym}, " +
			"{type}, " +
			"{fix}, " +
			"{sat}, " +
			"{hdop}, " +
			"{vdop}, " +
			"{pdop}, " +
			"{ageofdgpsdata}, " +
			"{dgpsid}" +
			"{course}" +
		");"
	);

	private static final Dctor<WayPoint> DCTOR = Dctor.of(
		Field.of("lat", wp -> wp.getLatitude().doubleValue()),
		Field.of("lon", wp -> wp.getLongitude().doubleValue()),
		Field.of("ele", wp -> wp.getElevation().map(Length::doubleValue).orElse(null)),
		Field.of("speed", wp -> wp.getSpeed().map(Speed::doubleValue).orElse(null)),
		Field.of("time", WayPoint::getTime),
		Field.of("magvar", wp -> wp.getMagneticVariation().map(Degrees::doubleValue).orElse(null)),
		Field.of("geoidheight", wp -> wp.getGeoidHeight().map(Length::doubleValue).orElse(null)),
		Field.of("name", WayPoint::getName),
		Field.of("cmt", WayPoint::getComment),
		Field.of("desc", WayPoint::getDescription),
		Field.of("src", WayPoint::getSource),
		Field.of("sym", WayPoint::getSymbol),
		Field.of("type", WayPoint::getType),
		Field.of("fix", wp -> wp.getFix().map(Fix::getValue).orElse(null)),
		Field.of("sat", wp -> wp.getSat().map(UInt::getValue).orElse(null)),
		Field.of("hdop", WayPoint::getHdop),
		Field.of("vdop", WayPoint::getVdop),
		Field.of("pdop", WayPoint::getPdop),
		Field.of("ageofdgpsdata", wp -> wp.getAgeOfGPSData().map(Duration::getSeconds).orElse(null)),
		Field.of("dgpsid", wp -> wp.getDGPSID().map(DGPSStation::intValue).orElse(null)),
		Field.of("course", wp -> wp.getCourse().map(Degrees::doubleValue).orElse(null))
	);

	public static Long insert(final WayPoint wp, final Connection conn)
		throws SQLException
	{
		if (wp == null) return null;

		final Long id = INSERT_QUERY.insert(wp, DCTOR, conn);
		insertLinks(id, wp.getLinks(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of(
		"INSERT INTO way_point_link(way_point_id, link_id " +
		"VALUES({way_point_id}, {link_id});"
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
				Field.ofValue("way_point_id", id),
				Field.of("link_id", LinkAccess::insert)
			),
			conn
		);
	}

}
