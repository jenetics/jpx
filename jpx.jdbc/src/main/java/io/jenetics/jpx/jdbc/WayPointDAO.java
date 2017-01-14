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

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jenetics.jpx.DGPSStation;
import io.jenetics.jpx.Degrees;
import io.jenetics.jpx.Fix;
import io.jenetics.jpx.Latitude;
import io.jenetics.jpx.Length;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Longitude;
import io.jenetics.jpx.Speed;
import io.jenetics.jpx.UInt;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class WayPointDAO
	extends DAO
	implements
		SelectBy<WayPoint>,
		Insert<WayPoint>,
		Update<WayPoint>,
		Delete
{

	private static final class Row {
		final Latitude latitude;
		final Longitude longitude;
		final Length elevation;
		final Speed speed;
		final ZonedDateTime time;
		final Degrees magneticVariation;
		final Length geoidHeight;
		final String name;
		final String comment;
		final String description;
		final String source;
		final String symbol;
		final String type;
		final Fix fix;
		final UInt sat;
		final Double hdop;
		final Double vdop;
		final Double pdop;
		final Duration ageOfGPSData;
		final DGPSStation dgpsID;

		Row(
			final Latitude latitude,
			final Longitude longitude,
			final Length elevation,
			final Speed speed,
			final ZonedDateTime time,
			final Degrees magneticVariation,
			final Length geoidHeight,
			final String name,
			final String comment,
			final String description,
			final String source,
			final String symbol,
			final String type,
			final Fix fix,
			final UInt sat,
			final Double hdop,
			final Double vdop,
			final Double pdop,
			final Duration ageOfGPSData,
			final DGPSStation dgpsID
		) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.elevation = elevation;
			this.speed = speed;
			this.time = time;
			this.magneticVariation = magneticVariation;
			this.geoidHeight = geoidHeight;
			this.name = name;
			this.comment = comment;
			this.description = description;
			this.source = source;
			this.symbol= symbol;
			this.type = type;
			this.fix = fix;
			this.sat = sat;
			this.hdop = hdop;
			this.vdop = vdop;
			this.pdop = pdop;
			this.ageOfGPSData = ageOfGPSData;
			this.dgpsID = dgpsID;
		}
	}

	public WayPointDAO(final Connection conn) {
		super(conn);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Row>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		new Row(
			rs.getLatitude("lat"),
			rs.getLongitude("lon"),
			rs.getLength("ele"),
			rs.getSpeed("speed"),
			rs.getZonedDateTime("time"),
			rs.getDegrees("magvar"),
			rs.getLength("geoidheight"),
			rs.getString("name"),
			rs.getString("cmt"),
			rs.getString("desc"),
			rs.getString("src"),
			rs.getString("sym"),
			rs.getString("type"),
			rs.getFix("fix"),
			rs.getUInt("sat"),
			rs.get(Double.class, "hdop"),
			rs.get(Double.class, "vdop"),
			rs.get(Double.class, "pdop"),
			rs.getDuration("ageofdgpsdata"),
			rs.getDGPSStation("dgpsid")
		)
	);


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public List<Stored<WayPoint>> select() throws SQLException {
		final String query =
			"SELECT id, " +
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
			"FROM way_point " +
			"ORDER BY id ASC";

		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
		return toWayPoint(rows);
	}

	private List<Stored<WayPoint>> toWayPoint(final List<Stored<Row>> rows)
		throws SQLException
	{
		final Map<Long, List<Link>> links = with(WayPointLinkDAO::new)
			.selectLinks(rows, Stored::id);

		return rows.stream()
			.map(row -> Stored.of(
				row.id(),
				WayPoint.of(
					row.value().latitude,
					row.value().longitude,
					row.value().elevation,
					row.value().speed,
					row.value().time,
					row.value().magneticVariation,
					row.value().geoidHeight,
					row.value().name,
					row.value().comment,
					row.value().description,
					row.value().source,
					links.get(row.id()),
					row.value().symbol,
					row.value().type,
					row.value().fix,
					row.value().sat,
					row.value().hdop,
					row.value().vdop,
					row.value().pdop,
					row.value().ageOfGPSData,
					row.value().dgpsID
				)
			))
			.collect(Collectors.toList());
	}

	@Override
	public <V, C> List<Stored<WayPoint>> selectByVals(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		return toWayPoint(selectRowsByVal(column, values));
	}

	private <V, C> List<Stored<Row>> selectRowsByVal(
		final Column<V, C> column,
		final Collection<V> values
	)
		throws SQLException
	{
		final String query =
			"SELECT id, " +
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
			"FROM way_point " +
			"WHERE "+column.name()+" IN ({values}) " +
			"ORDER BY id";

		return values.isEmpty()
			? Collections.emptyList()
			: SQL(query)
				.on(Param.values("values", values, column.mapper()))
				.as(RowParser.list());
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	@Override
	public List<Stored<WayPoint>> insert(final Collection<WayPoint> wayPoints)
		throws SQLException
	{
		final String query =
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
			")";

		final List<Stored<WayPoint>> inserted =
			Batch(query).insert(wayPoints, wp -> asList(
				Param.value("lat", wp.getLatitude()),
				Param.value("lon", wp.getLongitude()),
				Param.value("ele", wp.getElevation()),
				Param.value("speed", wp.getSpeed()),
				Param.value("time", wp.getTime()),
				Param.value("magvar", wp.getMagneticVariation()),
				Param.value("geoidheight", wp.getGeoidHeight()),
				Param.value("name", wp.getName()),
				Param.value("cmt", wp.getComment()),
				Param.value("desc", wp.getDescription()),
				Param.value("src", wp.getSource()),
				Param.value("sym", wp.getSymbol()),
				Param.value("type", wp.getType()),
				Param.value("fix", wp.getFix()),
				Param.value("sat", wp.getSat()),
				Param.value("hdop", wp.getHdop()),
				Param.value("vdop", wp.getVdop()),
				Param.value("pdop", wp.getPdop()),
				Param.value("ageofdgpsdata", wp.getAgeOfGPSData()),
				Param.value("dgpsid", wp.getDGPSID())
			));

		final Map<Link, Long> links = DAO
			.write(wayPoints, WayPoint::getLinks, with(LinkDAO::new)::put);

		final List<WayPointLink> wayPointLinks = inserted.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> WayPointLink.of(md.id(), links.get(l))))
			.collect(Collectors.toList());

		with(WayPointLinkDAO::new).insert(wayPointLinks);

		return inserted;
	}


	/* *************************************************************************
	 * UPDATE queries
	 **************************************************************************/

	@Override
	public List<Stored<WayPoint>> update(
		final Collection<Stored<WayPoint>> wayPoints
	)
		throws SQLException
	{
		final String query =
			"UPDATE way_point " +
				"SET lat = {lat}, " +
				"lon = {lon}, " +
				"ele = {ele}, " +
				"speed = {speed}, " +
				"time = {time}, " +
				"magvar = {magvar}, " +
				"geoidheight = {geoidheight}, " +
				"name = {name}, " +
				"cmt = {cmt}, " +
				"desc = {desc}, " +
				"src = {src}, " +
				"sym = {sym}, " +
				"type = {type}, " +
				"fix = {fix}, " +
				"sat = {sat}, " +
				"hdop = {hdop}, " +
				"vdop = {vdop}, " +
				"pdop = {pdop}, " +
				"ageofdgpsdata = {ageofdgpsdata}, " +
				"dgpsid = {dgpsid} " +
			"WHERE id = {id}";

		// Update way-points.
		Batch(query).update(wayPoints, wp -> asList(
			Param.value("id", wp.id()),
			Param.value("lat", wp.value().getLatitude()),
			Param.value("lon", wp.value().getLongitude()),
			Param.value("ele", wp.value().getElevation()),
			Param.value("speed", wp.value().getSpeed()),
			Param.value("time", wp.value().getTime()),
			Param.value("magvar", wp.value().getMagneticVariation()),
			Param.value("geoidheight", wp.value().getGeoidHeight()),
			Param.value("name", wp.value().getName()),
			Param.value("cmt", wp.value().getComment()),
			Param.value("desc", wp.value().getDescription()),
			Param.value("src", wp.value().getSource()),
			Param.value("sym", wp.value().getSymbol()),
			Param.value("type", wp.value().getType()),
			Param.value("fix", wp.value().getFix()),
			Param.value("sat", wp.value().getSat()),
			Param.value("hdop", wp.value().getHdop()),
			Param.value("vdop", wp.value().getVdop()),
			Param.value("pdop", wp.value().getPdop()),
			Param.value("ageofdgpsdata", wp.value().getAgeOfGPSData()),
			Param.value("dgpsid", wp.value().getDGPSID())
		));

		// Update metadata links.
		with(WayPointLinkDAO::new)
			.deleteByVals(Column.of("way_point_id", Stored::id), wayPoints);

		final Map<Link, Long> links = DAO.write(
			wayPoints,
			(ListMapper<Stored<WayPoint>, Link>)md -> md.value().getLinks(),
			with(LinkDAO::new)::put
		);

		final List<Pair<Long, Long>> wayPointLinks = wayPoints.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> Pair.of(md.id(), links.get(l))))
			.collect(Collectors.toList());

		with(WayPointLinkDAO::new)
			.insert(wayPointLinks, WayPointLink::of);

		return new ArrayList<>(wayPoints);
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
		final List<Stored<Row>> rows = selectRowsByVal(column, values);

		final int count;
		if (!rows.isEmpty()) {
			final String query =
				"DELETE FROM way_point WHERE id IN ({ids})";

			count = SQL(query)
				.on(Param.values("ids", rows, Stored::id))
				.execute();
		} else {
			count = 0;
		}

		return count;
	}

}
