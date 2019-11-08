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

import java.sql.Connection;
import java.sql.SQLException;

import io.jenetics.jpx.Link;

import io.jenetics.facilejdbc.Dctor;
import io.jenetics.facilejdbc.Query;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class LinkAccess {
	private LinkAccess() {}

	private static final Query INSERT_QUERY = Query.of(
		"INSERT INTO link(href, text, type) " +
		"VALUES(:href, :text, :type);"
	);

	private static final Dctor<Link> DCTOR = Dctor.of(
		field("href", Link::getHref),
		field("text", Link::getText),
		field("type", Link::getType)
	);

	public static Long insert(final Link link, final Connection conn)
		throws SQLException
	{
		return link != null
			? INSERT_QUERY
				.on(link, DCTOR)
				.executeInsert(conn)
				.orElseThrow()
			: null;
	}

}
