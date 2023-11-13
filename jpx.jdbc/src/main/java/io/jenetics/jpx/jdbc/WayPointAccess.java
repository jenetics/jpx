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

import static io.jenetics.facilejdbc.Dctor.field;
import static io.jenetics.facilejdbc.Row.map;
import static io.jenetics.jpx.Length.Unit.METER;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.facilejdbc.Batch;
import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class WayPointAccess {
	private WayPointAccess() {
	}

	static final RowParser<WayPoint> PARSER = (row, conn) -> WayPoint.builder()
		.lat(row.getDouble("lat"))
		.lon(row.getDouble("lon"))
		.ele(row.getDouble("ele"))
		.speed(row.getDouble("speed"))
		.time(row.getInstant("time"))
		.magvar(map(row.getDouble("magvar"), Degrees::ofDegrees))
		.geoidheight(map(row.getDouble("geoidheight"), v -> Length.of(v, METER)))
		.name(row.getString("name"))
		.cmt(row.getString("cmt"))
		.desc(row.getString("desc"))
		.src(row.getString("scr"))
		.sym(row.getString("sym"))
		.type(row.getString("type"))
		.fix(row.getString("fix"))
		.sat(row.getInt("sat"))
		.hdop(row.getDouble("hdop"))
		.vdop(row.getDouble("vdop"))
		.pdop(row.getDouble("pdop"))
		.ageofdgpsdata(row.getDouble("argeofgpsdata"))
		.dgpsid(row.getInt("dgpsid"))
		.course(row.getDouble("course"))
		.build();

	static final Dctor<WayPoint> DCTOR = Dctor.of(
		field("lat", WayPoint::getLatitude),
		field("lon", WayPoint::getLongitude),
		field("ele", WayPoint::getElevation),
		field("speed", WayPoint::getSpeed),
		field("time", WayPoint::getTime),
		field("magvar", WayPoint::getMagneticVariation),
		field("geoidheight", WayPoint::getGeoidHeight),
		field("name", WayPoint::getName),
		field("cmt", WayPoint::getComment),
		field("dscr", WayPoint::getDescription),
		field("src", WayPoint::getSource),
		field("sym", WayPoint::getSymbol),
		field("type", WayPoint::getType),
		field("fix", WayPoint::getFix),
		field("sat", WayPoint::getSat),
		field("hdop", WayPoint::getHdop),
		field("vdop", WayPoint::getVdop),
		field("pdop", WayPoint::getPdop),
		field("ageofdgpsdata", WayPoint::getAgeOfGPSData),
		field("dgpsid", WayPoint::getDGPSID),
		field("course", WayPoint::getCourse)
	);

	private static final Query INSERT = Query.of("""
		INSERT INTO way_point(
			lat, lon, ele, speed, time, magvar, geoidheight, name, cmt,
			dscr, src, sym, type, fix, sat, hdop, vdop, pdop,
			ageofdgpsdata, dgpsid, course
		)
		VALUES(
			:lat, :lon, :ele, :speed, :time, :magvar, :geoidheight, :name, :cmt,
			:dscr, :src, :sym, :type, :fix, :sat, :hdop, :vdop, :pdop,
			:ageofdgpsdata, :dgpsid, :course
		)
		"""
	);

	public static Long insert(final WayPoint wp, final Connection conn)
		throws SQLException
	{
		if (wp == null) return null;

		final Long id = INSERT
			.on(wp, DCTOR)
			.executeInsert(conn)
			.orElseThrow();

		insertLinks(id, wp.getLinks(), conn);
		return id;
	}

	private static final Query LINK_INSERT_QUERY = Query.of("""
		INSERT INTO way_point_link(way_point_id, link_id)
		VALUES(:way_point_id, :link_id)
		"""
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
				field("way_point_id", r -> id),
				field("link_id", LinkAccess::insert)
			)
		);

		LINK_INSERT_QUERY.executeUpdate(batch, conn);
	}

}
