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
package io.jenetics.jpx.jdbc.model;

import static io.jenetics.jpx.jdbc.internal.util.Lists.map;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import io.jenetics.jpx.Metadata;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class MetadataRow {
	public final long id;
	public String name;
	public String desc;
	public PersonRow person;
	public CopyrightRow copyright;
	public final List<LinkRow> links = new ArrayList<>();
	public ZonedDateTime time;
	public String keywords;
	public BoundsRow bounds;

	public MetadataRow(
		final long id,
		final String name,
		final String desc,
		final PersonRow person,
		final CopyrightRow copyright,
		final ZonedDateTime time,
		final String keywords,
		final BoundsRow bounds
	) {
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.person = person;
		this.copyright = copyright;
		this.time = time;
		this.keywords = keywords;
		this.bounds = bounds;
	}

	public Metadata toMetadata() {
		return Metadata.of(
			name,
			desc,
			person != null ? person.toPerson() : null,
			copyright != null ? copyright.toCopyright() : null,
			map(links, LinkRow::toLink),
			time,
			keywords,
			bounds != null ? bounds.toBounds() : null
		);
	}

}
