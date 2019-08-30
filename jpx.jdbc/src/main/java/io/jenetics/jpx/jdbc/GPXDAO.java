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

import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import io.jenetics.jpx.GPX;
import io.jenetics.jpx.jdbc.internal.db.DAO;
import io.jenetics.jpx.jdbc.internal.querily.Stored;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
//public final class GPXDAO extends DAO {
//
//	private static final class Row {
//		final String version;
//		final String creator;
//		final Long metadataID;
//
//		Row(
//			final String version,
//			final String creator,
//			final Long metadataID
//		) {
//			this.version = requireNonNull(version);
//			this.creator = requireNonNull(creator);
//			this.metadataID = metadataID;
//		}
//	}
//
//	/**
//	 * Parses one row of the "gpx" table.
//	 */
//	private static final io.jenetics.jpx.jdbc.internal.querily.RowParser<Stored<Row>>
//		RowParser = rs -> Stored.of(
//			rs.getLong("id"),
//			new Row(
//				rs.getString("version"),
//				rs.getString("creator"),
//				rs.get(Long.class, "metadata_id")
//			)
//		);
//
//	public GPXDAO(final Connection conn) {
//		super(conn);
//	}
//
//	/* *************************************************************************
//	 * SELECT queries
//	 **************************************************************************/
//
//	public List<Stored<GPX>> select() throws SQLException {
//		final String query =
//			"SELECT id, version, creator, metadata_id FROM gpx";
//
//		final List<Stored<Row>> rows = SQL(query).as(RowParser.list());
//
//		//final Map<Long, Metadata> metadata = with(MetadataDAO::new)
//		//	.selectByID(map(rows, row -> row.value().metadataID));
//
//		return null;
//	}
//
//
//	/* *************************************************************************
//	 * INSERT queries
//	 **************************************************************************/
//
//	public Stored<GPX> insert(final GPX gpx) throws SQLException {
//		requireNonNull(gpx);
//
//		//final Stored<Metadata> metadata = with(MetadataDAO::new).insert()
//
//		final String query =
//			"";
//
//		return null;
//	}
//
//}
