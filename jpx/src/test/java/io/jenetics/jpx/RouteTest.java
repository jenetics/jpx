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
public class RouteTest extends XMLStreamTestBase<Route> {

	@Override
	public Supplier<Route> factory(Random random) {
		return () -> nextRoute(random);
	}

	@Override
	protected Params<Route> params(final Random random) {
		return new Params<>(
			() -> nextRoute(random),
			Route.reader(),
			Route::write
		);
	}

	public static Route nextRoute(final Random random) {
		return Route.builder()
			.name(format("name_%s", Math.abs(random.nextLong())))
			.cmt(random.nextBoolean() ? format("comment_%s", Math.abs(random.nextLong())) : null)
			.desc(random.nextBoolean() ? format("description_%s", Math.abs(random.nextLong())) : null)
			.src(random.nextBoolean() ? format("source_%s", Math.abs(random.nextLong())) : null)
			.links(LinkTest.nextLinks(random))
			.number(random.nextBoolean() ? UInt.of(Math.abs(random.nextInt())) : null)
			.type(random.nextBoolean() ? format("type_%s", Math.abs(random.nextLong())) : null)
			.points(WayPointTest.nextWayPoints(random))
			.build();
	}

	public static List<Route> nextRoutes(final Random random) {
		return nextObjects(() -> nextRoute(random), random);
	}

	@Test
	public void filter() {
		final Route route = nextRoute(new Random());

		final Route filtered = route.toBuilder()
			.filter(wp -> wp.getLatitude().doubleValue() < 50)
			.build();

		for (int i = 0, n = filtered.getPoints().size(); i < n; ++i) {
			Assert.assertTrue(
				filtered.getPoints().get(i).getLatitude().doubleValue() < 50
			);
		}
	}

	@Test
	public void map() {
		final Route route = nextRoute(new Random(1));

		final Route mapped = route.toBuilder()
			.map(wp -> wp.toBuilder()
				.lat(wp.getLatitude().doubleValue() + 1)
				.build())
			.build();

		for (int i = 0, n = mapped.getPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getPoints().get(i).getLatitude().doubleValue(),
				route.getPoints().get(i).getLatitude().doubleValue() + 1
			);
		}
	}

	@Test
	public void flatMap() {
		final Route route = nextRoute(new Random(2));

		final Route mapped = route.toBuilder()
			.flatMap(wp -> Collections.singletonList(wp.toBuilder()
				.lat(wp.getLatitude().doubleValue() + 1)
				.build()))
			.build();

		for (int i = 0, n = mapped.getPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getPoints().get(i).getLatitude().doubleValue(),
				route.getPoints().get(i).getLatitude().doubleValue() + 1
			);
		}
	}

	@Test
	public void listMap() {
		final Route route = nextRoute(new Random(3));

		final Route mapped = route.toBuilder()
			.listMap(ListsTest::revert)
			.build();

		Assert.assertEquals(
			mapped.getPoints(),
			revert(route.getPoints())
		);
	}

	@Test
	public void toBuilder() {
		final Route object = nextRoute(new Random(3));

		Assert.assertEquals(
			object.toBuilder().build(),
			object
		);
		Assert.assertNotSame(
			object.toBuilder().build(),
			object
		);
	}

}
