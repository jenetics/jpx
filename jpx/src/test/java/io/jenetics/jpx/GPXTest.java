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

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.testng.Assert;
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
		final String rsc = "/io/jenetics/jpx/Gpx-full-sample.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(rsc)) {
			gpx = GPX.read(in);
		}

		final long length = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.count();
		Assert.assertEquals(length, 2747);
	}

}
