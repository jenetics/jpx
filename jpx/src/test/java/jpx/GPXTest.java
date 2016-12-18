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
package jpx;

import static java.lang.String.format;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Random;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GPXTest extends XMLStreamTestBase<GPX> {

	@Override
	protected Params<GPX> params(final Random random) {
		return new Params<>(
			() -> nextGPX(random),
			GPX.reader(),
			GPX::write
		);
	}

	public static GPX nextGPX(final Random random) {
		return GPX.of(
			format("creator_%s", random.nextInt(100)),
			format("version_%s", random.nextInt(100)),
			random.nextBoolean() ? MetadataTest.nextMetadata(random) : null,
			random.nextBoolean() ? WayPointTest.nextWayPoints(random) : null,
			random.nextBoolean() ? RouteTest.nextRoutes(random) : null,
			random.nextBoolean() ? TrackTest.nextTracks(random) : null
		);
	}

	@Test
	public void loadFromFile() throws IOException {
		try (InputStream in = getClass().getResourceAsStream("/jpx/Gpx-full-sample.gpx")) {
			final GPX gpx = GPX.read(in);
			//GPX.write(gpx, System.out);
		}
	}

	@Test
	public void usage() throws Exception {
		final GPX gpx = GPX.builder()
			.addTrack(track -> track
				.addSegment(segment -> segment
					.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
					.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
					.addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
			.build();

		GPX.write(gpx, "    ", System.out);


		final GPX gpx2 = GPX.builder()
			.metadata(m -> m.author("Franz Wilhelmstötter"))
			.addWayPoint(p -> p.lat(23.6).lon(13.5).ele(50))
			.addRoute(route -> route.name("route-1")
				.addPoint(p -> p.lon(12).lat(32).ele(12))
				.addPoint(p -> p.lon(12).lat(32).ele(13))
				.addPoint(p -> p.lon(12).lat(32).ele(14))
				.addPoint(p -> p.lon(12).lat(32).ele(15))
				.addPoint(p -> p.lon(12).lat(32).ele(16)))
			.addTrack(track -> track.name("track-1")
				.addSegment(segment -> segment
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12)))
				.addSegment(segment -> segment
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))))
			.addTrack(track -> track.name("track-2")
				.addSegment(segment -> segment
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12)))
				.addSegment(segment -> segment
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))
					.addPoint(p -> p.lon(12).lat(32).ele(12))))
			.build();

		try (FileOutputStream out = new FileOutputStream("/home/fwilhelm/gpx.xml")) {
			GPX.write(gpx, out);
		}

		GPX.write(gpx, "/home/fwilhelm/gpx.xml");
		final GPX gpx3 = GPX.read("/home/fwilhelm/gpx.xml");
		gpx3.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.forEach(System.out::println);
	}

}
