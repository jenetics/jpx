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
import static io.jenetics.jpx.ListsTest.revert;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;

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
	protected Params<Route> params(final Version version, final Random random) {
		return new Params<>(
			() -> nextRoute(random),
			Route.xmlReader(version),
			Route.xmlWriter(version)
		);
	}

	public static Route nextRoute(final Random random) {
		return Route.builder()
			.name(random.nextBoolean() ? format("name_%s", random.nextInt(100)) : null)
			.cmt(random.nextBoolean() ? format("comment_%s", random.nextInt(100)) : null)
			.desc(random.nextBoolean() ? format("description_%s", random.nextInt(100)) : null)
			.src(random.nextBoolean() ? format("source_%s", random.nextInt(100)) : null)
			.links(LinkTest.nextLinks(random))
			.number(random.nextBoolean() ? UInt.of(random.nextInt(10)) : null)
			.type(random.nextBoolean() ? format("type_%s", random.nextInt(100)) : null)
			.points(WayPointTest.nextWayPoints(random))
			.build();
	}

	public static List<Route> nextRoutes(final Random random) {
		return nextObjects(() -> nextRoute(random), random);
	}

	@Test
	public void withExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-route.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
		}

		Assert.assertEquals(gpx.getRoutes().size(), 1);

		Assert.assertEquals(
			gpx.getRoutes().get(0),
			Route.builder()
				.name("name_97")
				.cmt("comment_69")
				.build()
		);
		Assert.assertTrue(XML.equals(
			gpx.getRoutes().get(0).getExtensions().get(),
			XML.parse("<extensions><foo xmlns=\"http://www.topografix.com/GPX/1/1\">asdf</foo><foo xmlns=\"http://www.topografix.com/GPX/1/1\">asdf</foo></extensions>")
		));
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

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(Route.class)
			.withIgnoredFields("_extensions")
			.verify();
	}

	@Test(invocationCount = 10)
	public void serialize() throws IOException, ClassNotFoundException {
		final Object object = nextRoute(new Random());
		Serialization.test(object);
	}

}
