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
package io.jenetics.jpx.jdbc.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;

import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;
import io.jenetics.jpx.jdbc.internal.querily.RowParser;
import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
final class CopyrightDAO {

	private CopyrightDAO() {
	}

	/**
	 * The link row parser which creates a {@link Copyright} object from a given
	 * DB row.
	 */
	private static final RowParser<Stored<Copyright>> ROW_PARSER = rs -> Stored.of(
		rs.getLong("id"),
		Copyright.of(
			rs.getString("author"),
			rs.getInt("year"),
			rs.getString("license")
		)
	);

	/**
	 * Insert the given copyright list into the DB.
	 *
	 * @param copyright the links to insert
	 * @return return the stored copyrights
	 * @throws SQLException if inserting fails
	 */
	public Long insert(final Copyright copyright, final Connection conn)
		throws SQLException
	{
		final String sql =
			"INSERT INTO copyright(author, year, license) " +
			"VALUES({author}, {year}, {license})";

		return Query.of(sql).insert(
			copyright,
			Dctor.of(
				Field.of("author", Copyright::getAuthor),
				Field.of("year", c -> c.getYear().map(Year::getValue).orElse(null)),
				Field.of("license", Copyright::getLicense)
			),
			conn
		);
	}

}
