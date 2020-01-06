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
import static io.jenetics.jpx.ListsTest.revert;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class TrackTest extends XMLStreamTestBase<Track> {

	@Override
	public Supplier<Track> factory(Random random) {
		return () -> nextTrack(random);
	}

	@Override
	protected Params<Track> params(final Version version, final Random random) {
		return new Params<>(
			() -> nextTrack(random),
			Track.xmlReader(version),
			Track.xmlWriter(version)
		);
	}

	public static Track nextTrack(final Random random) {
		return Track.of(
			random.nextBoolean()
				? format("name_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? format("comment_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? format("description_%s", random.nextInt(100))
				: null,
			random.nextBoolean()
				? format("source_%s", random.nextInt(100))
				: null,
			LinkTest.nextLinks(random),
			random.nextBoolean()
				? UInt.of(random.nextInt(100))
				: null,
			random.nextBoolean()
				? format("type_%s", random.nextInt(100))
				: null,
			TrackSegmentTest.nextTrackSegments(random)
		);
	}

	public static List<Track> nextTracks(final Random random) {
		return nextObjects(() -> nextTrack(random), random);
	}

	@Test
	public void withExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-track.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
		}

		Assert.assertEquals(gpx.getTracks().size(), 1);

		Assert.assertEquals(
			gpx.getTracks().get(0),
			Track.builder()
				.name("name_97")
				.cmt("comment_69")
				.build()
		);

		Assert.assertTrue(XML.equals(
			gpx.getTracks().get(0).getExtensions().get(),
			XML.parse("<extensions xmlns=\"http://www.topografix.com/GPX/1/1\"><foo>asdf</foo><foo>asdf</foo></extensions>")
		));
	}

	@Test
	public void filter() {
		final Track track = nextTrack(new Random());

		final Track filtered = track.toBuilder()
			.filter(segment -> segment.getPoints().size() > 10)
			.build();

		filtered.getSegments().forEach(segment ->
			Assert.assertTrue(segment.getPoints().size() > 10)
		);
	}

	@Test
	public void map() {
		final Track track = nextTrack(new Random());

		final Track mapped = track.toBuilder()
			.map(segment -> segment.toBuilder()
				.points(revert(segment.getPoints()))
				.build())
			.build();

		for (int i = 0, n = mapped.getSegments().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getSegments().get(i).getPoints(),
				revert(track.getSegments().get(i).getPoints())
			);
		}
	}

	@Test
	public void flatMap() {
		final Track track = nextTrack(new Random());

		final Track mapped = track.toBuilder()
			.flatMap(segment -> Collections.singletonList(segment.toBuilder()
				.points(revert(segment.getPoints()))
				.build()))
			.build();

		for (int i = 0, n = mapped.getSegments().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getSegments().get(i).getPoints(),
				revert(track.getSegments().get(i).getPoints())
			);
		}
	}

	@Test
	public void listMap() {
		final Track track = nextTrack(new Random());

		final Track mapped = track.toBuilder()
			.listMap(ListsTest::revert)
			.build();

		Assert.assertEquals(
			mapped.getSegments(),
			revert(track.getSegments())
		);
	}

	@Test
	public void toBuilder() {
		final Track object = nextTrack(new Random());

		Assert.assertEquals(
			object.toBuilder().build(),
			object
		);
		Assert.assertNotSame(
			object.toBuilder().build(),
			object
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Track.class)
			.withIgnoredFields("_extensions")
			.verify();
	}

	@Test(invocationCount = 10)
	public void serialize() throws IOException, ClassNotFoundException {
		final Object object = nextTrack(new Random());
		Serialization.test(object);
	}

}
