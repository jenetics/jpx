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

import static java.util.Collections.singletonList;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import io.jenetics.jpx.jdbc.internal.db.Stored;

/**
 * Interface for common update functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Update<T> {

	/**
	 * Updates the given list of already inserted objects.
	 *
	 * @param values the values to update
	 * @return the updated values
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<T>> update(final Collection<Stored<T>> values)
		throws SQLException;


	/**
	 * Update the given Object.
	 *
	 * @param value the link to update
	 * @return the updated value
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default Stored<T> update(final Stored<T> value) throws SQLException {
		return update(singletonList(value)).get(0);
	}

}
