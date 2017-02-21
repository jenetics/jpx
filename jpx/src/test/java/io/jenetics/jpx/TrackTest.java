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

import static io.jenetics.jpx.ListsTest.revert;
import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

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
	protected Params<Track> params(final Random random) {
		return new Params<>(
			() -> nextTrack(random),
			Track.reader(),
			Track::write
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

}
