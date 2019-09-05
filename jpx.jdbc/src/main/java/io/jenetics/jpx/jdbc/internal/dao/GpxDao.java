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
import java.time.Year;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Link;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class GpxDao {
	private GpxDao() {
	}


	public static Long insert(final Metadata md, final Connection conn)
		throws SQLException
	{
		final Long id = Query.of(
			"INSERT INTO metadata(" +
				"name, " +
				"desc, " +
				"person_id, " +
				"copyright_id, " +
				"time, " +
				"keywords, " +
				"bounds_id" +
			") " +
			"VALUES(" +
				"{name}, " +
				"{desc}, " +
				"{person_id}, " +
				"{copyright_id}, " +
				"{time}, " +
				"{keywords}, " +
				"{bounds_id}" +
			")"
		).executeInsert(
			md,
			Dctor.of(
				Field.of("name", Metadata::getName),
				Field.of("desc", Metadata::getDescription),
				Field.of("person_id", m -> insert(m.getAuthor().orElse(null), conn)),
				Field.of("copyright_id", m -> insert(m.getCopyright().orElse(null), conn)),
				Field.of("time", Metadata::getTime),
				Field.of("bounds_id", m -> insert(m.getBounds().orElse(null), conn))
			),
			conn
		);

		Query.of(
			"INSERT INTO metadata_link(metadata_id, link_id " +
			"VALUES({metadata_id}, {link_id});"
		).executeInserts(
			md.getLinks(),
			Dctor.of(
				Field.of("metadata_id", l -> id),
				Field.of("link_id", l -> insert(l, conn))
			),
			conn
		);

		return id;
	}

	public static Long insert(final Bounds bounds, final Connection conn)
		throws SQLException
	{
		if (bounds == null) return null;

		return Query.of(
			"INSERT INTO bounds(minlat, minlon, maxlat, maxlon) " +
			"VALUES({minlat}, {minlon}, {maxlat}, {maxlon})"
		).executeInsert(
			bounds,
			Dctor.of(
				Field.of("minlat", b -> b.getMinLatitude().doubleValue()),
				Field.of("minlon", b -> b.getMinLongitude().doubleValue()),
				Field.of("maxlat", b -> b.getMaxLatitude().doubleValue()),
				Field.of("maxlon", b -> b.getMinLongitude().doubleValue())
			),
			conn
		);
	}

	public static Long insert(final Copyright copyright, final Connection conn)
		throws SQLException
	{
		if (copyright == null) return null;

		return Query.of(
			"INSERT INTO copyright(author, year, license) " +
			"VALUES({author}, {year}, {license})"
		).executeInsert(
			copyright,
			Dctor.of(
				Field.of("author", Copyright::getAuthor),
				Field.of("year", c -> c.getYear().map(Year::getValue).orElse(null)),
				Field.of("license", Copyright::getLicense)
			),
			conn
		);
	}

	public static Long insert(final Link link, final Connection conn)
		throws SQLException
	{
		if (link == null) return null;

		return Query.of(
			"INSERT INTO link(href, text, type) " +
			"VALUES({href}, {text}, {type});"
		).executeInsert(
			link,
			Dctor.of(
				Field.of("href", Link::getHref),
				Field.of("text", Link::getText),
				Field.of("type", Link::getType)
			),
			conn
		);
	}

	public static Long insert(final Person person, final Connection conn)
		throws SQLException
	{
		if (person == null || person.isEmpty()) return null;

		return Query.of(
			"INSERT INTO person(name, email, link_id) " +
			"VALUES({name}, {email}, {link_id});"
		).executeInsert(
			person,
			Dctor.of(
				Field.of("name", Person::getName),
				Field.of("email", Person::getEmail),
				Field.of("link_id", p -> insert(p.getLink().orElse(null), conn))
			),
			conn
		);
	}

}
