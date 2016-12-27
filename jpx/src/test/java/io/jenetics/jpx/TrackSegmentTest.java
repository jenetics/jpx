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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
@Test
public class TrackSegmentTest extends XMLStreamTestBase<TrackSegment> {

	@Override
	public Supplier<TrackSegment> factory(Random random) {
		return () -> nextTrackSegment(random);
	}

	@Override
	protected Params<TrackSegment> params(final Random random) {
		return new Params<>(
			() -> nextTrackSegment(random),
			TrackSegment.reader(),
			TrackSegment::write
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

}
