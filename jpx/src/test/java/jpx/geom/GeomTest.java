/*
 * Java Genetic Algorithm Library (@__identifier__@).
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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package jpx.geom;

import jpx.Point;
import jpx.WayPoint;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 */
public class GeomTest {

	private final static Geom GEOM = Geom.DEFAULT;

	@Test
	public void distance() {
		final Point start = WayPoint.of(47.2692124, 11.4041024);
		final Point end = WayPoint.of(47.3502, 11.70584);

		Assert.assertEquals(
			GEOM.distance(start, end).doubleValue(),
			24528.356073554987
		);
		Assert.assertEquals(
			GEOM.distance(end, start).doubleValue(),
			24528.356073555155
		);
		Assert.assertEquals(
			GEOM.distance(end, end).doubleValue(),
			0.0
		);
	}

}
