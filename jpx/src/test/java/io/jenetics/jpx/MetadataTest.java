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
import static io.jenetics.jpx.ZonedDateTimesTest.nextZonedDataTime;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class MetadataTest extends XMLStreamTestBase<Metadata> {

	@Override
	public Supplier<Metadata> factory(Random random) {
		return () -> nextMetadata(random);
	}

	@Override
	protected Params<Metadata> params(final Random random) {
		return new Params<>(
			() -> nextMetadata(random),
			Metadata.reader(),
			Metadata.WRITER
		);
	}

	public static Metadata nextMetadata(final Random random) {
		return Metadata.of(
			random.nextBoolean()
				? format("name_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? format("description_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? PersonTest.nextPerson(random)
				: null,
			random.nextBoolean()
				? CopyrightTest.nextCopyright(random)
				: null,
			LinkTest.nextLinks(random),
			random.nextBoolean()
				? nextZonedDataTime(random)
				: null,
			random.nextBoolean()
				? format("keywords_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? BoundsTest.nextBounds(random)
				: null
		);
	}

	@Test
	public void builder() {
		final Metadata metadata = Metadata.builder()
			.author("Franz Wilhelmstötter")
			.addLink(Link.of("http://jenetics.io/jpx"))
			.build();
	}

	@Test
	public void toBuilder() {
		final Metadata metadata = Metadata.builder()
			.author("Franz Wilhelmstötter")
			.addLink(Link.of("http://jenetics.io/jpx"))
			.build();

		Assert.assertEquals(
			metadata.toBuilder().build(),
			metadata
		);
		Assert.assertNotSame(
			metadata.toBuilder().build(),
			metadata
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Metadata.class).verify();
	}

	@Test(invocationCount = 10)
	public void serialize() throws IOException, ClassNotFoundException {
		final Object object = nextMetadata(new Random());
		Serialization.test(object);
	}

}
