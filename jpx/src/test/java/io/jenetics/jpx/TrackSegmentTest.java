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

import static java.util.Locale.ENGLISH;
import static io.jenetics.jpx.ListsTest.revert;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class TrackSegmentTest extends XMLStreamTestBase<TrackSegment> {

	@Override
	public Supplier<TrackSegment> factory(Random random) {
		return () -> nextTrackSegment(random);
	}

	@Override
	protected Params<TrackSegment> params(final Version version, final Random random) {
		final var format = NumberFormat.getNumberInstance(ENGLISH);
		final Function<String, Length> lengthParser = string ->
			Length.parse(string, format);

		return new Params<>(
			() -> nextTrackSegment(random),
			TrackSegment.xmlReader(version, lengthParser),
			TrackSegment.xmlWriter(version, Formats::format)
		);
	}

	public static TrackSegment nextTrackSegment(final Random random) {
		return TrackSegment.of(WayPointTest.nextWayPoints(random));
	}

	public static List<TrackSegment> nextTrackSegments(final Random random) {
		final List<TrackSegment> segments = new ArrayList<>();
		for (int i = 0, n = random.nextInt(20); i < n; ++i) {
			segments.add(nextTrackSegment(random));
		}

		return segments;
	}

	@Test
	public void withExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-tracksegment.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		Assert.assertEquals(gpx.getTracks().size(), 1);
		Assert.assertEquals(gpx.getTracks().get(0).getSegments().size(), 1);
		Assert.assertTrue(XML.equals(
			gpx.getTracks().get(0).getSegments().get(0).getExtensions().get(),
			XML.parse("<extensions xmlns=\"http://www.topografix.com/GPX/1/1\"><foo>asdf</foo><foo>asdf</foo></extensions>")
		));
	}

	@Test
	public void filter() {
		final TrackSegment segment = TrackSegment.of(
			IntStream.range(0, 90)
				.mapToObj(i -> WayPoint.builder().build(i, i))
				.toList()
		);

		final TrackSegment filtered = segment.toBuilder()
			.filter(wp -> wp.getLatitude().doubleValue() < 50)
			.build();

		Assert.assertEquals(filtered.getPoints().size(), 50);
		for (int i = 0, n = filtered.getPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				filtered.getPoints().get(i).getLatitude().doubleValue(),
				(double)i
			);
		}
	}

	@Test
	public void map() {
		final TrackSegment segment = TrackSegment.of(
			IntStream.range(0, 50)
				.mapToObj(i -> WayPoint.builder().build(i, i))
				.toList()
		);

		final TrackSegment mapped = segment.toBuilder()
			.map(wp -> wp.toBuilder()
				.lat(wp.getLatitude().doubleValue() + 1)
				.build())
			.build();

		for (int i = 0, n = mapped.getPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getPoints().get(i).getLatitude().doubleValue(),
				(double)(i + 1)
			);
		}
	}

	@Test
	public void flatMap() {
		final TrackSegment segment = TrackSegment.of(
			IntStream.range(0, 25)
				.mapToObj(i -> WayPoint.builder().build(i, i))
				.toList()
		);

		final TrackSegment mapped = segment.toBuilder()
			.flatMap(wp -> Collections.singletonList(wp.toBuilder()
				.lat(wp.getLatitude().doubleValue() + 1)
				.build()))
			.build();

		for (int i = 0, n = mapped.getPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getPoints().get(i).getLatitude().doubleValue(),
				(double)(i + 1)
			);
		}
	}

	@Test
	public void listMap() {
		final TrackSegment segment = TrackSegment.of(
			IntStream.range(0, 25)
				.mapToObj(i -> WayPoint.builder().build(i, i))
				.toList()
		);

		final TrackSegment mapped = segment.toBuilder()
			.listMap(ListsTest::revert)
			.build();

		Assert.assertEquals(
			mapped.getPoints(),
			revert(segment.getPoints())
		);
	}

	@Test
	public void toBuilder() {
		final TrackSegment object = TrackSegment.of(
			IntStream.range(0, 25)
				.mapToObj(i -> WayPoint.builder().build(i, i))
				.toList()
		);

		Assert.assertEquals(
			object.toBuilder().build(),
			object
		);
		Assert.assertNotEquals(
			System.identityHashCode(object.toBuilder().build()),
			System.identityHashCode(object)
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(TrackSegment.class)
			.withIgnoredFields("_extensions")
			.verify();
	}

}
