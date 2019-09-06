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

import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.jdbc.internal.querily.Dctor;
import io.jenetics.jpx.jdbc.internal.querily.Dctor.Field;
import io.jenetics.jpx.jdbc.internal.querily.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class BoundsAccess {
	private BoundsAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO bounds(minlat, minlon, maxlat, maxlon) " +
		"VALUES({minlat}, {minlon}, {maxlat}, {maxlon})"
	);

	private static final Dctor<Bounds> DCTOR = Dctor.of(
		Field.of("minlat", b -> b.getMinLatitude().doubleValue()),
		Field.of("minlon", b -> b.getMinLongitude().doubleValue()),
		Field.of("maxlat", b -> b.getMaxLatitude().doubleValue()),
		Field.of("maxlon", b -> b.getMaxLongitude().doubleValue())
	);

	public static Long insert(final Bounds bounds, final Connection conn)
		throws SQLException
	{
		return bounds != null
			? INSERT_QUERY.insert(bounds, DCTOR, conn)
			: null;
	}

}
