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
package io.jenetics.jpx;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class LinkTest
	extends XMLStreamTestBase<Link>
{

	@Override
	public Supplier<Link> factory(Random random) {
		return () -> nextLink(random);
	}

	@Override
	protected Params<Link> params(final Random random) {
		return new Params<>(
			() -> nextLink(random),
			Link.reader(),
			Link::write
		);
	}

	public static Link nextLink(final Random random) {
		return Link.of(
			format("http://link_%d", Math.abs(random.nextLong())),
			random.nextBoolean()
				? format("text_%s", Math.abs(random.nextLong()))
				: null,
			random.nextBoolean()
				? format("type_%s", Math.abs(random.nextLong()))
				: null
		);
	}

	public static List<Link> nextLinks(final Random random) {
		final List<Link> links = new ArrayList<>();
		for (int i = 0, n = random.nextInt(20); i < n; ++i) {
			links.add(LinkTest.nextLink(random));
		}

		return links;
	}

}
