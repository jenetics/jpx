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

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;
import static io.jenetics.jpx.GPXTest.nextGPX;
import static io.jenetics.jpx.TrackTest.nextTrack;
import static io.jenetics.jpx.WayPointTest.nextWayPoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class FiltersTest {

	@Test
	public void mergeSegments() {
		final GPX gpx = nextGPX(new Random(1));

		final GPX merged = gpx.toBuilder()
			.trackFilter()
				.map(track -> track.toBuilder()
					.listMap(Filters::mergeSegments)
					.filter(TrackSegment::nonEmpty)
					.build())
				.build()
			.build();

		for (Track track : merged.getTracks()) {
			Assert.assertEquals(track.getSegments().size(), 1);
		}
	}

	@Test
	public void mergeTracks() {
		final GPX gpx = nextGPX(new Random(1)).toBuilder()
			.addTrack(nextTrack(new Random(2)))
			.addTrack(nextTrack(new Random(3)))
			.addTrack(nextTrack(new Random(4)))
			.build();

		final GPX merged = gpx.toBuilder()
			.trackFilter()
			.listMap(Filters::mergeTracks)
			.filter(Track::nonEmpty)
			.build()
			.build();

		Assert.assertEquals(merged.getTracks().size(), 1);

		final int segments = merged.getTracks().stream()
			.mapToInt(t -> t.getSegments().size())
			.sum();

		Assert.assertEquals(segments, 38);
	}

	@Test
	public void fullyMergeTracks() {
		final GPX gpx = nextGPX(new Random(1)).toBuilder()
			.addTrack(nextTrack(new Random(2)))
			.addTrack(nextTrack(new Random(3)))
			.addTrack(nextTrack(new Random(4)))
			.build();

		final GPX merged = gpx.toBuilder()
			.trackFilter()
				.listMap(Filters::fullyMergeTracks)
				.filter(Track::nonEmpty)
				.build()
			.build();

		Assert.assertEquals(merged.getTracks().size(), 1);

		final int segments = merged.getTracks().stream()
			.mapToInt(t -> t.getSegments().size())
			.sum();

		Assert.assertEquals(segments, 1);
	}

	public void splitByDay() {
		final Random random = new Random(1);
		final ZonedDateTime time = ZonedDateTime.of(2017, 1, 1, 0, 0, 0, 0, UTC);

		final AtomicInteger count = new AtomicInteger();
		final List<WayPoint> points = Stream.generate(() -> nextWayPoint(random))
			.limit(100)
			.map(wp -> wp.toBuilder()
				.time(time.plusHours(count.incrementAndGet()))
				.build())
			.collect(toList());

		for (TrackSegment list : Filters.splitByDay(TrackSegment.of(points))) {
			System.out.println("------------------------");
			for (WayPoint point : list) {
				System.out.println(point.getTime() + ": " + point);
			}
		}
	}

	@Test
	public void nonEmptyFilter() throws IOException {
		final GPX gpx = GPX.read(new ByteArrayInputStream((
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<gpx version=\"1.1\" creator=\"JPX - https://github.com/jenetics/jpx\" " +
			"xmlns=\"http://www.topografix.com/GPX/1/1\">\n" +
			"    <metadata></metadata>\n" +
			"    <trk><trkseg></trkseg></trk>\n" +
			"    <trk>\n" +
			"        <trkseg></trkseg>\n" +
			"        <trkseg>\n" +
			"            <trkpt lat=\"21.0\" lon=\"23.0\">\n" +
			"                <ele>12.0</ele>\n" +
			"            </trkpt>\n" +
			"        </trkseg>\n" +
			"    </trk>\n" +
			"    <trk></trk>\n" +
			"</gpx>").getBytes()));

		final GPX nonEmpty = Filters.nonEmptyGPX(gpx);

		final GPX expected = GPX.builder()
			.addTrack(track -> track
				.addSegment(segment -> segment
					.addPoint(wp -> wp.lat(21.0).lon(23.0).ele(12.0))))
			.build();

		Assert.assertEquals(nonEmpty, expected);
	}

}
