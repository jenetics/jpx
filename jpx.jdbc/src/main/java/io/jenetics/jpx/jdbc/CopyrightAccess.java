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

import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Person;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;
import io.jenetics.facilejdbc.RowParser;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class CopyrightAccess {
	private CopyrightAccess() {}

	private static final Query SELECT = Query.of(
		"SELECT id, author, year, license " +
		"FROM copyright " +
		"WHERE id = :id"
	);

	private static final Query INSERT = Query.of(
		"INSERT INTO copyright(author, year, license) " +
		"VALUES(:author, :year, :license)"
	);

	private static final RowParser<Copyright> PARSER = (row, conn) -> Copyright.of(
		row.getString("author"),
		Year.of(row.getInt("year")),
		URI.create(row.getString("license"))
	);

	private static final Dctor<Copyright> DCTOR = Dctor.of(
		field("author", Copyright::getAuthor),
		field("year", c -> c.getYear().map(Year::getValue)),
		field("license", Copyright::getLicense)
	);

	public static Copyright selectById(final Long id, final Connection conn)
		throws SQLException
	{
		return id != null
			? SELECT
				.on(value("id", id))
				.as(PARSER.singleNullable(), conn)
			: null;
	}

	public static Long insert(final Copyright copyright, final Connection conn)
		throws SQLException
	{
		return copyright != null
			? INSERT
				.on(copyright, DCTOR)
				.executeInsert(conn)
				.orElseThrow()
			: null;
	}

}
