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
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.db.Column;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.db.Delete;
import io.jenetics.jpx.jdbc.internal.db.Inserter;
import io.jenetics.jpx.jdbc.internal.db.ListMapper;
import io.jenetics.jpx.jdbc.internal.db.OptionMapper;
import io.jenetics.jpx.jdbc.internal.db.Param;
import io.jenetics.jpx.jdbc.internal.db.SelectBy;
import io.jenetics.jpx.jdbc.internal.db.Stored;
import io.jenetics.jpx.jdbc.internal.db.Updater;
import io.jenetics.jpx.jdbc.internal.util.Pair;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public class MetadataDAO
//	extends DAO
//	implements
//	SelectBy<Metadata>,
//	Inserter<Metadata>,
//	Updater<Metadata>,
//	Delete
//{
//
//	/**
//	 * Represents a row in the "metadata" tables.
//	 */
//	private static final class Row {
//		final String name;
//		final String description;
//		final Long personID;
//		final Long copyrightID;
//		final ZonedDateTime time;
//		final String keywords;
//		final Long boundsID;
//
//		Row(
//			final String name,
//			final String description,
//			final Long personID,
//			final Long copyrightID,
//			final ZonedDateTime time,
//			final String keywords,
//			final Long boundsID
//		) {
//			this.name = name;
//			this.description = description;
//			this.personID = personID;
//			this.copyrightID = copyrightID;
//			this.time = time;
//			this.keywords = keywords;
//			this.boundsID = boundsID;
//		}
//	}
//
//
//	public MetadataDAO(final Connection connection) {
//		super(connection);
//	}
//
//	/**
//	 * The metadata row parser which creates a {@link Metadata} object from a
//	 * given DB row.
//	 */
//	private static final io.jenetics.jpx.jdbc.internal.db.RowParser<Stored<Row>> RowParser = rs -> Stored.of(
//		rs.getLong("id"),
//		new Row(
//			rs.getString("name"),
//			rs.getString("desc"),
//			rs.get(Long.class, "person_id"),
//			rs.get(Long.class, "copyright_id"),
//			rs.getZonedDateTime("time"),
//			rs.getString("keywords"),
//			rs.get(Long.class, "bounds_id")
//		)
//	);
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
//	public List<Stored<Metadata>> select() throws SQLException {
//		final String query =
//			"SELECT id, " +
//				"name, " +
//				"desc, " +
//				"person_id, " +
//				"copyright_id, " +
//				"time, " +
//				"keywords, " +
//				"bounds_id " +
//			"FROM metadata " +
//			"ORDER BY id";
//
//		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
//		return toMetadata(rows);
//	}
//
//	private List<Stored<Metadata>> toMetadata(final Collection<Stored<Row>> rows)
//		throws SQLException
//	{
//		final Map<Long, Person> persons = with(PersonDAO::new)
//			.selectByVals(Column.of("person.id", row -> row.value().personID), rows)
//			.stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		final Map<Long, Copyright> copyrights = with(CopyrightDAO::new)
//			.selectByVals(Column.of("id", row -> row.value().copyrightID), rows)
//			.stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		final Map<Long, Bounds> bounds = with(BoundsDAO::new)
//			.selectByVals(Column.of("id", row -> row.value().boundsID), rows)
//			.stream()
//			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
//
//		final Map<Long, List<Link>> links = with(MetadataLinkDAO::new)
//			.selectLinks(rows, Stored::id);
//
//		return rows.stream()
//			.map(row -> Stored.of(
//				row.id(),
//				Metadata.of(
//					row.value().name,
//					row.value().description,
//					persons.get(row.value().personID),
//					copyrights.get(row.value().copyrightID),
//					links.get(row.id()),
//					row.value().time,
//					row.value().keywords,
//					bounds.get(row.value().boundsID)
//				)
//			))
//			.collect(Collectors.toList());
//	}
//
//	@Override
//	public <V, C> List<Stored<Metadata>> selectByVals(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		return toMetadata(selectRowsByVal(column, values));
//	}
//
//	private <V, C> List<Stored<Row>> selectRowsByVal(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		final String query =
//			"SELECT id, " +
//				"name, " +
//				"desc, " +
//				"person_id, " +
//				"copyright_id, " +
//				"time, " +
//				"keywords, " +
//				"bounds_id " +
//			"FROM metadata " +
//			"WHERE "+column.name()+" IN ({values}) " +
//			"ORDER BY id";
//
//		return values.isEmpty()
//			? Collections.emptyList()
//			: SQL(query)
//				.on(Param.values("values", values, column.mapper()))
//				.as(RowParser.list());
//	}
//
//	/* *************************************************************************
//	 * INSERT queries
//	 **************************************************************************/
//
//	/**
//	 * Insert the given person list into the DB.
//	 *
//	 * @param metadata the persons to insert
//	 * @return return the stored persons
//	 * @throws SQLException if inserting fails
//	 */
//	@Override
//	public List<Stored<Metadata>> insert(final Collection<Metadata> metadata)
//		throws SQLException
//	{
//		final Map<Person, Long> persons = DAO
//			.write(metadata, Metadata::getAuthor, with(PersonDAO::new)::put);
//
//		final Map<Copyright, Long> copyrights = DAO
//			.write(metadata, Metadata::getCopyright, with(CopyrightDAO::new)::insert);
//
//		final Map<Bounds, Long> bounds = DAO
//			.write(metadata, Metadata::getBounds, with(BoundsDAO::new)::insert);
//
//		final String query =
//			"INSERT INTO metadata(" +
//				"name, " +
//				"desc, " +
//				"person_id, " +
//				"copyright_id, " +
//				"time, " +
//				"keywords, " +
//				"bounds_id" +
//			") " +
//			"VALUES(" +
//				"{name}, " +
//				"{desc}, " +
//				"{person_id}, " +
//				"{copyright_id}, " +
//				"{time}, " +
//				"{keywords}, " +
//				"{bounds_id}" +
//			")";
//
//		final List<Stored<Metadata>> inserted =
//			Batch(query).insert(metadata, md -> asList(
//				Param.value("name", md.getName()),
//				Param.value("desc", md.getDescription()),
//				Param.value("person_id", md.getAuthor().map(persons::get)),
//				Param.value("copyright_id", md.getCopyright().map(copyrights::get)),
//				Param.value("time", md.getTime()),
//				Param.value("keywords", md.getKeywords()),
//				Param.value("bounds_id", md.getBounds().map(bounds::get))
//			));
//
//		final Map<Link, Long> links = DAO
//			.write(metadata, Metadata::getLinks, with(LinkDAO::new)::put);
//
//		final List<Pair<Long, Long>> metadataLinks = inserted.stream()
//			.flatMap(md -> md.value().getLinks().stream()
//				.map(l -> Pair.of(md.id(), links.get(l))))
//			.collect(Collectors.toList());
//
//		with(MetadataLinkDAO::new)
//			.insert(metadataLinks, MetadataLink::of);
//
//		return inserted;
//	}
//
//	/* *************************************************************************
//	 * UPDATE queries
//	 **************************************************************************/
//
//	@Override
//	public List<Stored<Metadata>> update(final Collection<Stored<Metadata>> metadata)
//		throws SQLException
//	{
//		final List<Stored<Row>> rows =
//			selectRowsByVal(Column.of("id", Stored::id), metadata);
//
//		// Update author.
//		final Map<Person, Long> persons = DAO.write(
//			metadata,
//			(OptionMapper<Stored<Metadata>, Person>)
//				md -> md.value().getAuthor(),
//			with(PersonDAO::new)::put
//		);
//
//		// Update copyright.
//		final Map<Copyright, Long> copyrights = DAO.write(
//			metadata,
//			(OptionMapper<Stored<Metadata>, Copyright>)
//				md -> md.value().getCopyright(),
//			with(CopyrightDAO::new)::insert
//		);
//
//		// Update bounds.
//		final Map<Bounds, Long> bounds = DAO.write(
//			metadata,
//			(OptionMapper<Stored<Metadata>, Bounds>)
//				md -> md.value().getBounds(),
//			with(BoundsDAO::new)::insert
//		);
//
//		final String query =
//			"UPDATE metadata " +
//				"SET name = {name}, " +
//				"desc = {desc}, " +
//				"person_id = {person_id}, " +
//				"copyright_id = {copyright_id}, " +
//				"time = {time}, " +
//				"keywords = {keywords}, " +
//				"bounds_id = {bounds_id}" +
//			"WHERE id = {id}";
//
//		// Update metadata.
//		Batch(query).update(metadata, md -> asList(
//			Param.value("id", md.id()),
//			Param.value("name", md.value().getName()),
//			Param.value("desc", md.value().getDescription()),
//			Param.value("person_id", md.value().getAuthor().map(persons::get)),
//			Param.value("copyright_id", md.value().getCopyright().map(copyrights::get)),
//			Param.value("time", md.value().getTime()),
//			Param.value("keywords", md.value().getKeywords()),
//			Param.value("bounds_id", md.value().getBounds().map(bounds::get))
//		));
//
//		// Update metadata links.
//		with(MetadataLinkDAO::new)
//			.deleteByVals(Column.of("metadata_id", Stored::id), rows);
//
//		final Map<Link, Long> links = DAO.write(
//			metadata,
//			(ListMapper<Stored<Metadata>, Link>)md -> md.value().getLinks(),
//			with(LinkDAO::new)::put
//		);
//
//		final List<Pair<Long, Long>> metadataLinks = metadata.stream()
//			.flatMap(md -> md.value().getLinks().stream()
//				.map(l -> Pair.of(md.id(), links.get(l))))
//			.collect(Collectors.toList());
//
//		with(MetadataLinkDAO::new)
//			.insert(metadataLinks, MetadataLink::of);
//
//		// Delete old copyright.
//		with(CopyrightDAO::new).deleteByVals(
//			Column.of("id", row -> row.value().copyrightID), rows
//		);
//
//		// Delete old bounds.
//		with(BoundsDAO::new).deleteByVals(
//			Column.of("id", row -> row.value().boundsID), rows
//		);
//
//		return new ArrayList<>(metadata);
//	}
//
//	/* *************************************************************************
//	 * DELETE queries
//	 **************************************************************************/
//
//	@Override
//	public <V, C> int deleteByVals(
//		final Column<V, C> column,
//		final Collection<V> values
//	)
//		throws SQLException
//	{
//		final List<Stored<Row>> rows = selectRowsByVal(column, values);
//
//		final int count;
//		if (!rows.isEmpty()) {
//			final String query =
//				"DELETE FROM metadata WHERE id IN ({ids})";
//
//			count = SQL(query)
//				.on(Param.values("ids", rows, Stored::id))
//				.execute();
//		} else {
//			count = 0;
//		}
//
//		with(CopyrightDAO::new)
//			.deleteByVals(Column.of("id", row -> row.value().copyrightID), rows);
//
//		with(BoundsDAO::new)
//			.deleteByVals(Column.of("id", row -> row.value().boundsID), rows);
//
//		return count;
//	}
//
//}
