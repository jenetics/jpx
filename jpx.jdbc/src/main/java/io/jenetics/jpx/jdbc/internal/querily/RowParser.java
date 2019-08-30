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
package io.jenetics.jpx.jdbc.internal.querily;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Converts one row from the given {@link ResultSet} into a data object from
 * the given type.
 *
 * @param <T> the data object type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
@FunctionalInterface
public interface RowParser<T> {

	/**
	 * Converts the row on the current cursor position into a data object.
	 *
	 * @param rs the data source
	 * @return the stored data object
	 * @throws SQLException if reading of the current row fails
	 */
	public T parse(final Results rs) throws SQLException;

	/**
	 * Return a new parser which expects at least one result.
	 *
	 * @return a new parser which expects at least one result
	 */
	public default RowParser<T> single() {
		return rs -> {
			if (rs.next()) {
				return parse(rs);
			}
			throw new NoSuchElementException();
		};
	}

	/**
	 * Return a new parser which parses a single selection result.
	 *
	 * @return a new parser which parses a single selection result
	 */
	public default RowParser<Optional<T>> singleOpt() {
		return rs -> rs.next()
			? Optional.ofNullable(parse(rs))
			: Optional.empty();
	}

	/**
	 * Return a new parser witch parses a the whole selection result.
	 *
	 * @return a new parser witch parses a the whole selection result
	 */
	public default RowParser<List<T>> list() {
		return rs -> {
			final List<T> result = new ArrayList<>();
			while (rs.next()) {
				result.add(parse(rs));
			}

			return result;
		};
	}

}
