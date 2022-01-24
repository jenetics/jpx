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
package io.jenetics.jpx.geom;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Length;
import io.jenetics.jpx.Point;
import io.jenetics.jpx.WayPoint;
import io.jenetics.jpx.WayPointTest;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GeoidTest {

	private static final double EPSILON = 0.000000001;

	private final static Geoid GEOID = Geoid.DEFAULT;

	@Test
	public void distance() {
		final Point start = WayPoint.of(47.2692124, 11.4041024);
		final Point end = WayPoint.of(47.3502, 11.70584);

		Assert.assertEquals(
			GEOID.distance(start, end).doubleValue(),
			24528.356073554987,
			EPSILON
		);
		Assert.assertEquals(
			GEOID.distance(end, start).doubleValue(),
			24528.356073555155,
			EPSILON
		);
		Assert.assertEquals(
			GEOID.distance(end, end).doubleValue(),
			0.0,
			EPSILON
		);
	}

	@Test(dataProvider = "pointSizes")
	public void collectPathLength(final int size) {
		final Random random = new Random(123);
		final List<WayPoint> points = Stream
			.generate(() -> WayPointTest.nextWayPoint(random))
			.limit(size)
			.toList();

		Assert.assertEquals(
			pathLength(points),
			points.stream()
				.collect(GEOID.toPathLength())
				.doubleValue()
		);
	}

	private static double pathLength(final List<WayPoint> points)  {
		final DoubleAdder length = new DoubleAdder();
		for (int i = 1; i < points.size(); ++i) {
			length.add(GEOID.distance(
				points.get(i - 1),
				points.get(i)).doubleValue()
			);
		}

		return length.doubleValue();
	}

	@Test(dataProvider = "pointSizes")
	public void collectTourLength(final int size) {
		final Random random = new Random(123);
		final List<WayPoint> points = Stream
			.generate(() -> WayPointTest.nextWayPoint(random))
			.limit(size)
			.toList();

		Assert.assertEquals(
			tourLength(points),
			points.stream()
				.collect(GEOID.toTourLength())
				.doubleValue()
		);
	}

	private static double tourLength(final List<WayPoint> points)  {
		final DoubleAdder length = new DoubleAdder();
		for (int i = 0; i < points.size(); ++i) {
			length.add(GEOID.distance(
				points.get(i),
				points.get((i + 1)%points.size())).doubleValue()
			);
		}

		return length.doubleValue();
	}

	@DataProvider(name = "pointSizes")
	public Object[][] pointSizes() {
		return new Object[][] {
			{0}, {1}, {2}, {3}, {11}, {27}, {100}, {1000}
		};
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void parallelPointStream() {
		Length length = Stream.generate(() -> WayPointTest.nextWayPoint(new Random()))
			.limit(1000)
			.parallel()
			.collect(GEOID.toPathLength());

		Assert.assertNotNull(length);
	}

}
