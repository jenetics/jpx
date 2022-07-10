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
import static java.util.Locale.ENGLISH;
import static io.jenetics.jpx.ZonedDateTimesTest.nextZonedDataTime;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.jenetics.jpx.GPX.Version;
import io.jenetics.jpx.Length.Unit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
@Test
public class WayPointTest extends XMLStreamTestBase<WayPoint> {

	@Override
	public Supplier<WayPoint> factory(Random random) {
		return () -> nextWayPoint(random);
	}

	@Override
	protected Params<WayPoint> params(final Version version, final Random random) {
		final var format = NumberFormat.getNumberInstance(ENGLISH);
		final Function<String, Length> lengthParser = string ->
			Length.parse(string, format);

		return new Params<>(
			() -> nextWayPoint(random),
			WayPoint.xmlReader(version, "wpt", lengthParser),
			WayPoint.xmlWriter(version, "wpt", Formats::format)
		);
	}

	public static WayPoint nextWayPoint(final Random random) {
		return WayPoint.builder()
			.ele(random.nextBoolean() ? Length.of(random.nextInt(1000), Unit.METER) : null)
			.speed(random.nextBoolean() ? Speed.of(nextDouble(random)*100, Speed.Unit.METERS_PER_SECOND) : null)
			.time(random.nextBoolean()
				? nextZonedDataTime(random)
				: null)
			.magvar(random.nextBoolean() ? Degrees.ofDegrees(nextDouble(random)*10) : null)
			.geoidheight(random.nextBoolean() ? Length.of(random.nextInt(1000), Unit.METER) : null)
			.name(random.nextBoolean() ? format("name_%s", random.nextInt(100)) : null)
			.cmt(random.nextBoolean() ? format("comment_%s", random.nextInt(100)) : null)
			.desc(random.nextBoolean() ? format("description_%s", random.nextInt(100)) : null)
			.src(random.nextBoolean() ? format("source_%s", random.nextInt(100)) : null)
			.links(LinkTest.nextLinks(random))
			.sym(random.nextBoolean() ? format("symbol_%s", random.nextInt(100)) : null)
			.type(random.nextBoolean() ? format("type_%s", random.nextInt(100)) : null)
			.fix(random.nextBoolean() ? Fix.values()[random.nextInt(Fix.values().length)] : null)
			.sat(random.nextBoolean() ? UInt.of(random.nextInt(100)) : null)
			.hdop(random.nextBoolean() ? nextDouble(random) + 2: null)
			.vdop(random.nextBoolean() ? nextDouble(random) + 2: null)
			.pdop(random.nextBoolean() ? nextDouble(random) + 2: null)
			.ageofdgpsdata(random.nextBoolean() ? Duration.ofSeconds(random.nextInt(1000)) : null)
			.dgpsid(random.nextBoolean() ? DGPSStation.of(random.nextInt(100)) : null)
			.build(48 + nextDouble(random)*2, 16 + nextDouble(random)*2);
	}

	private static double nextDouble(final RandomGenerator random) {
		//final int scale = 10_000;
		//return random.nextInt(scale)/(double)scale;
		return random.nextDouble();
	}

	public static List<WayPoint> nextWayPoints(final Random random) {
		final List<WayPoint> points = new ArrayList<>();
		for (int i = 0, n = random.nextInt(20); i < n; ++i) {
			points.add(nextWayPoint(random));
		}
		return points;
	}

	@Test
	public void withExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-waypoint.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		Assert.assertEquals(gpx.getWayPoints().size(), 1);

		Assert.assertEquals(
			gpx.getWayPoints().get(0),
			WayPoint.builder()
				.lat(48.2081743).lon(16.3738189)
				.ele(171)
				.name("Wien")
				.build()
		);
		Assert.assertTrue(XML.equals(
			gpx.getWayPoints().get(0).getExtensions().orElseThrow(),
			XML.parse("<extensions xmlns=\"http://www.topografix.com/GPX/1/1\"><foo>asdf</foo><foo>asdf</foo></extensions>")
		));
	}

	@Test
	public void toBuilder() {
		final WayPoint object = nextWayPoint(new Random());

		Assert.assertEquals(
			object.toBuilder().build(),
			object
		);
		Assert.assertNotEquals(
			System.identityHashCode(object.toBuilder().build()),
			System.identityHashCode(object)
		);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(WayPoint.class)
			.withIgnoredFields("_extensions")
			.verify();
	}

	@Test(invocationCount = 25)
	public void serialize() throws IOException, ClassNotFoundException {
		final WayPoint object = nextWayPoint(new Random());
		Serialization.test(object);

		/*
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final DataOutputStream dout = new DataOutputStream(out);
		WayPoint.of(1.1, 1.1).write(dout);
		dout.flush();
		System.out.println(out.size());
		*/
	}

}
