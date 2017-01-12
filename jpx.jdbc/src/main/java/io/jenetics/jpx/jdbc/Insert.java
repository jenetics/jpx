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

/**
 * This interface defines insertion methods for a given row type.
 *
 * @param <T> the row type
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public interface Insert<T> {

	/**
	 * Insert the given objects into the DB.
	 *
	 * @param values the objects to insert
	 * @return return the inserted objects
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public List<Stored<T>> insert(final Collection<T> values)
		throws SQLException;


	/**
	 * Insert the given object into the DB.
	 *
	 * @param value the value to insert
	 * @return return the stored value
	 * @throws SQLException if the operation fails
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public default Stored<T> insert(final T value)
		throws SQLException
	{
		return insert(singletonList(value)).get(0);
	}

}
