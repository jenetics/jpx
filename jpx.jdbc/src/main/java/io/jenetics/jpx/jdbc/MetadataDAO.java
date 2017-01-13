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
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static io.jenetics.jpx.jdbc.Lists.flatMap;
import static io.jenetics.jpx.jdbc.Lists.map;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MetadataDAO extends DAO {

	/**
	 * Represents a row in the "metadata" tables.
	 */
	private static final class Row {
		final String name;
		final String description;
		final Long personID;
		final Long copyrightID;
		final ZonedDateTime time;
		final String keywords;
		final Long boundsID;

		Row(
			final String name,
			final String description,
			final Long personID,
			final Long copyrightID,
			final ZonedDateTime time,
			final String keywords,
			final Long boundsID
		) {
			this.name = name;
			this.description = description;
			this.personID = personID;
			this.copyrightID = copyrightID;
			this.time = time;
			this.keywords = keywords;
			this.boundsID = boundsID;
		}
	}


	public MetadataDAO(final Connection connection) {
		super(connection);
	}

	/**
	 * The link row parser which creates a {@link Link} object from a given DB
	 * row.
	 */
	private static final RowParser<Stored<Row>> RowParser = rs -> Stored.of(
		rs.getLong("id"),
		new Row(
			rs.getString("name"),
			rs.getString("desc"),
			rs.get(Long.class, "person_id"),
			rs.get(Long.class, "copyright_id"),
			rs.getZonedDateTime("time"),
			rs.getString("keywords"),
			rs.get(Long.class, "bounds_id")
		)
	);

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	public List<Stored<Metadata>> select() throws SQLException {
		final String query =
			"SELECT id, " +
				"name, " +
				"desc, " +
				"person_id, " +
				"copyright_id, " +
				"time, " +
				"keywords, " +
				"bound_id " +
			"FROM metadata";

		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
		return toMetadata(rows);
	}

	private List<Stored<Metadata>> toMetadata(final Collection<Stored<Row>> rows)
		throws SQLException
	{
		final Map<Long, Person> persons = with(PersonDAO::new)
			.selectByVals(Column.of("id", Stored::id), rows).stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

		final Map<Long, Copyright> copyrights = with(CopyrightDAO::new)
			.selectByVals(Column.of("id", r -> r.value().copyrightID), rows)
			.stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

		final Map<Long, Bounds> bounds = with(BoundsDAO::new)
			.selectByID(map(rows, r -> r.value().boundsID))
			.stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));

		final Map<Long, List<Link>> links = with(MetadataLinkDAO::new)
			.selectLinksByMetadataID(map(rows, Stored::id));

		return rows.stream()
			.map(row -> Stored.of(
				row.id(),
				Metadata.of(
					row.value().name,
					row.value().description,
					persons.get(row.value().personID),
					copyrights.get(row.value().copyrightID),
					links.get(row.id()),
					row.value().time,
					row.value().keywords,
					bounds.get(row.value().boundsID)
				)
			))
			.collect(Collectors.toList());
	}

	/**
	 * Select the {@link Metadata} with the given DB IDs.
	 *
	 * @param ids the metadata DB IDs.
	 * @return the selected metadata which maps the DB ID to the given metadata
	 * @throws SQLException if the DB operation fails
	 */
	public Map<Long, Metadata> selectByID(final List<Long> ids)
		throws SQLException
	{
		return toMetadata(selectRowsByID(ids)).stream()
			.collect(toMap(Stored::id, Stored::value, (a, b) -> b));
	}

	private List<Stored<Row>> selectRowsByID(final List<Long> ids)
		throws SQLException
	{
		final String query =
			"SELECT id, " +
				"name, " +
				"desc, " +
				"person_id, " +
				"copyright_id, " +
				"time, " +
				"keywords, " +
				"bound_id " +
			"FROM metadata " +
			"WHERE id IN ({ids})";

		return SQL(query).on(Param.values("ids", ids)).as(RowParser.list());
	}

	/* *************************************************************************
	 * INSERT queries
	 **************************************************************************/

	/**
	 * Insert the given person list into the DB.
	 *
	 * @param metadata the persons to insert
	 * @return return the stored persons
	 * @throws SQLException if inserting fails
	 */
	public List<Stored<Metadata>> insert(final List<Metadata> metadata)
		throws SQLException
	{
		final Map<Person, Long> persons = DAO
			.write(metadata, Metadata::getAuthor, with(PersonDAO::new)::put);

		final Map<Copyright, Long> copyrights = DAO
			.write(metadata, Metadata::getCopyright, with(CopyrightDAO::new)::put);

		final Map<Bounds, Long> bounds = DAO
			.write(metadata, Metadata::getBounds, with(BoundsDAO::new)::insert);

		final String query =
			"INSERT INTO person(name, email, link_id) " +
			"VALUES({name}, {email}, {link_id});";

		final List<Stored<Metadata>> inserted =
			Batch(query).insert(metadata, md -> asList(
				Param.value("name", md.getName()),
				Param.value("desc", md.getDescription()),
				Param.value("person_id", md.getAuthor().map(persons::get)),
				Param.value("copyright_id", md.getCopyright().map(copyrights::get)),
				Param.value("time", md.getTime()),
				Param.value("keywords", md.getKeywords()),
				Param.value("bounds_id", md.getBounds().map(bounds::get))
			));

		final Map<Link, Long> links = DAO
			.write(metadata, Metadata::getLinks, with(LinkDAO::new)::put);

		final List<Pair<Long, Long>> metadataLinks = inserted.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> Pair.of(md.id(), links.get(l))))
			.collect(Collectors.toList());

		with(MetadataLinkDAO::new).insert(metadataLinks);

		return inserted;
	}

	public Stored<Metadata> insert(final Metadata metadata) throws SQLException {
		return insert(singletonList(metadata)).get(0);
	}

	/* *************************************************************************
	 * DELETE queries
	 **************************************************************************/

	public int deleteByID(final List<Long> ids) throws SQLException {
		final List<Stored<Row>> rows = selectRowsByID(ids);

		final int deleted = SQL("DELETE FROM metadata WHERE id IN ({ids})")
			.on(Param.values("ids", ids))
			.execute();

		with(BoundsDAO::new).deleteByID(
			flatMap(rows,
				(OptionMapper<Stored<Row>, Long>)
					row -> Optional.ofNullable(row.value().boundsID))
		);

		return deleted;
	}

}
