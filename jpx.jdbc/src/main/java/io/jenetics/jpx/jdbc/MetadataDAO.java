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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package io.jenetics.jpx.jdbc;

import static java.util.Arrays.asList;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
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

	/*
CREATE TABLE metadata(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255),
	`desc` TEXT,
	person_id BIGINT REFERENCES person(id),
	copyright_id BIGINT,
	time TIMESTAMP,
	keywords VARCHAR(255),
	bounds_id BIGINT REFERENCES bounds(id)
);
CREATE INDEX i_metadata_name ON metadata(name);
CREATE INDEX i_metadata_keywords ON metadata(keywords);

CREATE TABLE metadata_link(
	metadata_id BIGINT NOT NULL REFERENCES metadata(id) ON DELETE CASCADE,
	link_id BIGINT NOT NULL REFERENCES link(id),

	CONSTRAINT c_metadata_link_metadata_id_link_id UNIQUE (metadata_id, link_id)
);
	 */

	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

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
			.set(metadata, Metadata::getAuthor, with(PersonDAO::new)::put);

		final Map<Copyright, Long> copyrights = DAO
			.set(metadata, Metadata::getCopyright, with(CopyrightDAO::new)::put);

		final Map<Bounds, Long> bounds = DAO
			.set(metadata, Metadata::getBounds, with(BoundsDAO::new)::insert);

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
			.set(metadata, Metadata::getLinks, with(LinkDAO::new)::put);

		final List<Pair<Long, Long>> metadataLinks = inserted.stream()
			.flatMap(md -> md.value().getLinks().stream()
				.map(l -> Pair.of(md.id(), links.get(l))))
			.collect(Collectors.toList());;

		Batch(
			"INSERT INTO metadata_link(metadata_id, link_id) " +
			"VALUES({metadata_id}, {link_id});"
		).set(metadataLinks, mdl -> asList(
			Param.value("metadata_id", mdl._1),
			Param.value("link_id", mdl._2)
		));

		return inserted;
	}


	/* *************************************************************************
	 * SELECT queries
	 **************************************************************************/

	/**
	 * Select all available copyrights.
	 *
	 * @return all stored copyrights
	 * @throws SQLException if the select fails
	 */
	/*
	public List<Stored<Metadata>> select() throws SQLException {
		final String query =
			"SELECT id, " +
				"name, " +
				"description, " +
				"person.name AS person_name, " +
				"person.email AS person_email, " +
				"link.href AS link_href, " +
				"link.text AS link_text, " +
				"link.type AS link_type " +
				"person.";

		return SQL(query).as(RowParser.list());
	}
	*/

}
