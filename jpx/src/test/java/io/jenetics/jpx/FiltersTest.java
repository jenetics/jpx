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

import static io.jenetics.jpx.GPXTest.nextGPX;
import static io.jenetics.jpx.TrackTest.nextTrack;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

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

	@Test
	public void nonEmptyFilter() throws IOException {
		final GPX gpx = GPX.Reader.DEFAULT.read(new ByteArrayInputStream(("""
				<?xml version="1.0" encoding="UTF-8"?>
				<gpx
				    version="1.1" creator="JPX - https://github.com/jenetics/jpx"
				    xmlns="http://www.topografix.com/GPX/1/1"
				>
				    <metadata></metadata>
				    <trk><trkseg></trkseg></trk>
				    <trk>
				        <trkseg></trkseg>
				        <trkseg>
				            <trkpt lat="21.0" lon="23.0">
				                <ele>12.0</ele>
				            </trkpt>
				        </trkseg>
				    </trk>
				    <trk></trk>
				</gpx>""").getBytes()));

		final GPX nonEmpty = Filters.nonEmptyGPX(gpx);

		final GPX expected = GPX.builder()
			.addTrack(track -> track
				.addSegment(segment -> segment
					.addPoint(wp -> wp.lat(21.0).lon(23.0).ele(12.0))))
			.build();

		Assert.assertEquals(nonEmpty, expected);
	}

}
