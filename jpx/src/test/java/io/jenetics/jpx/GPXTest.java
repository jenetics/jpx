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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.jenetics.jpx.Length.Unit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GPXTest extends XMLStreamTestBase<GPX> {

	@Override
	public Supplier<GPX> factory(Random random) {
		return () -> nextGPX(random);
	}

	@Override
	protected Params<GPX> params(final Random random) {
		return new Params<>(
			() -> nextGPX(random),
			GPX.READER,
			GPX.WRITER
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

	@Test(dataProvider = "validEmptyElementsFiles")
	public void validEmptyElements(final String resource, final GPX expected)
		throws IOException
	{
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.read(in);
			Assert.assertEquals(gpx, expected);
		}
	}

	@DataProvider(name = "validEmptyElementsFiles")
	public Object[][] validEmptyElementsFiles() {
		return new Object[][] {
			{
				"/io/jenetics/jpx/empty-gpx.xml",
				GPX.builder("JPX").build()
			},
			{
				"/io/jenetics/jpx/empty-metadata.xml",
				GPX.builder("JPX")
					.metadata(md -> {})
					.build()
			},
			{
				"/io/jenetics/jpx/empty-ele.xml",
				GPX.builder("JPX")
					.addWayPoint(p -> p.lat(12.12).lon(12.12))
					.build()
			},
			{
				"/io/jenetics/jpx/empty-route.xml",
				GPX.builder("JPX")
					.addRoute(route -> {})
					.build()
			},
			{
				"/io/jenetics/jpx/empty-track.xml",
				GPX.builder("JPX")
					.addTrack(track -> {})
					.build()
			},
			{
				"/io/jenetics/jpx/empty-track-segment.xml",
				GPX.builder("JPX")
					.addTrack(track -> track.addSegment(segment -> {}))
					.build()
			}
		};
	}

	@Test(dataProvider = "invalidGPXFiles", expectedExceptions = {IOException.class})
	public void invalidGPX(final String resource) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			GPX.read(in);
		}
	}

	@DataProvider(name = "invalidGPXFiles")
	public Object[][] invalidGPXFiles() {
		return new Object[][] {
			{"/io/jenetics/jpx/empty-waypoint.xml"},
			{"/io/jenetics/jpx/invalid-latlon.xml"}
		};
	}

	@Test
	public void lenientRead() throws IOException {
		final String resource = "/io/jenetics/jpx/invalid-latlon.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.read(in, true);

			Assert.assertTrue(gpx.getMetadata().isPresent());
			Assert.assertFalse(gpx.getMetadata().get().getBounds().isPresent());

			final int length = (int)gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.count();

			Assert.assertEquals(length, 4);
		}
	}

	@Test(expectedExceptions = {IOException.class})
	public void strictRead() throws IOException {
		final String resource = "/io/jenetics/jpx/invalid-latlon.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			GPX.read(in, false);
		}
	}

	@Test
	public void loadFullSampleFile() throws IOException, XMLStreamException {
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

		final WayPoint point = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.findFirst()
			.orElseThrow(NoSuchElementException::new);

		Assert.assertEquals(
			point.getLatitude(),
			Latitude.ofDegrees(55.753572)
		);
		Assert.assertEquals(
			point.getLongitude(),
			Longitude.ofDegrees(37.808250)
		);
		Assert.assertEquals(
			point.getElevation(),
			Optional.of(Length.of(135, Unit.METER))
		);
		Assert.assertEquals(
			point.getTime(),
			Optional.of(ZonedDateTimeFormat.parse("2009-05-19T04:00:30Z"))
		);
		Assert.assertEquals(
			point.getFix(),
			Optional.of(Fix.DIM_2)
		);
		Assert.assertEquals(
			point.getSat(),
			Optional.of(UInt.of(3))
		);
		Assert.assertEquals(
			point.getHdop(),
			Optional.of(2.61)
		);
		Assert.assertEquals(
			point.getVdop(),
			Optional.of(1.0)
		);
		Assert.assertEquals(
			point.getPdop(),
			Optional.of(2.79)
		);
	}

	@Test
	public void loadAustria() throws IOException {
		final String rsc = "/io/jenetics/jpx/Austria.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(rsc)) {
			gpx = GPX.read(in);
		}

		Assert.assertEquals(
			gpx.getCreator(),
			"Jenetics TSP"
		);

		Assert.assertEquals(
			gpx.getWayPoints().size(),
			82
		);
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void emptyWayPointException() {
		WayPoint.builder().build();
	}

	@Test
	public void wayPointFilter() {
		final GPX gpx = nextGPX(new Random());

		final GPX filtered = gpx.toBuilder()
			.wayPointFilter()
				.filter(wp -> wp.getLatitude().doubleValue() < 50)
				.build()
			.build();

		for (int i = 0, n = filtered.getWayPoints().size(); i < n; ++i) {
			Assert.assertTrue(
				filtered.getWayPoints().get(i).getLatitude().doubleValue() < 50
			);
		}
	}

	@Test
	public void wayPointMap() {
		final GPX gpx = nextGPX(new Random(1));

		final GPX mapped = gpx.toBuilder()
			.wayPointFilter()
				.map(wp -> wp.toBuilder()
					.lat(wp.getLatitude().doubleValue() + 1)
					.build())
				.build()
			.build();

		for (int i = 0, n = mapped.getWayPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getWayPoints().get(i).getLatitude().doubleValue(),
				gpx.getWayPoints().get(i).getLatitude().doubleValue() + 1
			);
		}
	}

	@Test
	public void wayPointFlatMap() {
		final GPX gpx = nextGPX(new Random(1));

		final GPX mapped = gpx.toBuilder()
			.wayPointFilter()
				.flatMap(wp -> Collections.singletonList(wp.toBuilder()
					.lat(wp.getLatitude().doubleValue() + 1)
					.build()))
				.build()
			.build();

		for (int i = 0, n = mapped.getWayPoints().size(); i < n; ++i) {
			Assert.assertEquals(
				mapped.getWayPoints().get(i).getLatitude().doubleValue(),
				gpx.getWayPoints().get(i).getLatitude().doubleValue() + 1
			);
		}
	}

	@Test
	public void wayPointListMap() {
		final GPX gpx = nextGPX(new Random(1));

		final GPX mapped = gpx.toBuilder()
			.wayPointFilter()
				.listMap(ListsTest::revert)
				.build()
			.build();

		Assert.assertEquals(
			mapped.getWayPoints(),
			revert(gpx.getWayPoints())
		);
	}

	@Test
	public void toBuilder() {
		final GPX gpx = nextGPX(new Random(1));

		Assert.assertEquals(
			gpx.toBuilder().build(),
			gpx
		);
		Assert.assertNotSame(
			gpx.toBuilder().build(),
			gpx
		);
	}

	@Test
	public void readWriteRandomIndentedGPX() throws IOException {
		final Random random = new Random(1234);
		for (int i = 0; i < 10; ++i) {
			final GPX gpx = nextGPX(random);

			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			GPX.write(gpx, bout, "    ");

			final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			final GPX read = GPX.read(bin);

			Assert.assertEquals(read, gpx);
		}
	}

	@Test
	public void readWriteRandomNonIndentedGPX() throws IOException {
		final Random random = new Random(1234);
		for (int i = 0; i < 10; ++i) {
			final GPX gpx = nextGPX(random);

			final ByteArrayOutputStream bout = new ByteArrayOutputStream();
			GPX.write(gpx, bout, "    ");

			final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			final GPX read = GPX.read(bin);


			if (!read.equals(gpx)) {
				System.out.println(new String(bout.toByteArray()));
			}
			Assert.assertEquals(read, gpx);
		}
	}

	@Test(dataProvider = "readWriteGPX")
	public void readWrite(final String resource) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx1 = GPX.read(in);

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			GPX.write(gpx1, out, "    ");
			final GPX gpx2 = GPX.read(new ByteArrayInputStream(out.toByteArray()));

			Assert.assertEquals(gpx1, gpx2);
		}
	}

	@DataProvider(name = "readWriteGPX")
	public Object[][] readWriteGPX() {
		return new Object[][] {
			{"/io/jenetics/jpx/ISSUE-38.gpx.xml"}
		};
	}

	@Test(timeOut = 2000)
	public void issue49_NPEForInvalidGPX() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-49.gpx";

		try (InputStream in = getClass().getResourceAsStream(resource)) {
			GPX.read(in);
			Assert.assertFalse(true, "GXP.read must throw.");
		} catch (IOException e) {
			Assert.assertEquals(e.getCause().getClass(), XMLStreamException.class);
			Assert.assertTrue(e.getMessage().contains("Unexpected element"));
		}
	}

	@Test(timeOut = 2000)
	public void issue49_NPEForInvalidGPXLenient() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-49.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in, true);
		}

		Assert.assertEquals(gpx.getWayPoints().size(), 1);
		Assert.assertEquals(
			gpx.getWayPoints().get(0),
			WayPoint.builder()
				.lat(51.39709).lon(4.501519).name("START")
				.build()
		);

		final List<WayPoint> points = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.collect(Collectors.toList());

		Assert.assertEquals(points.size(), 26);
	}

	@Test(timeOut = 2000)
	public void issue51_XMLComment() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-51.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
		}

		Assert.assertTrue(gpx.getMetadata().isPresent());
		final Metadata md = gpx.getMetadata().get();
		Assert.assertFalse(md.getName().isPresent());
		Assert.assertTrue(md.getLinks().isEmpty());
		Assert.assertEquals(gpx.getWayPoints().size(), 3);
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(GPX.class).verify();
	}


	@Test(invocationCount = 5)
	public void serialize() throws IOException, ClassNotFoundException {
		final GPX object = nextGPX(new Random());
		Serialization.test(object);
	}

	@Test
	public void compatibleSerialization() throws IOException, ClassNotFoundException {
		final String baseDir = "src/test/resources/io/jenetics/jpx/serialization";

		final Random random = new Random(123);
		for (int i = 0; i < 15; ++i) {
			final GPX gpx = nextGPX(random);

			final GPX read = GPX.read(Paths.get(baseDir, format("gpx_%d.xml", i)));
			try {
				Assert.assertEquals(read, gpx);
			} catch (AssertionError e) {
				GPX.write(read, Paths.get(baseDir, format("gpx_%d(1).xml", i)), "    ");
			}


			try (InputStream fin = new FileInputStream(new File(baseDir, format("gpx_%d.obj", i)));
				 ObjectInputStream oin = new ObjectInputStream(fin))
			{
				Assert.assertEquals(oin.readObject(), gpx);
			}
		}
	}

	public static void main(final String[] args) throws IOException {
		final String baseDir = "jpx/src/test/resources/io/jenetics/jpx/serialization";

		final Random random = new Random(123);
		for (int i = 0; i < 15; ++i) {
			final GPX gpx = nextGPX(random);

			GPX.write(gpx, Paths.get(baseDir, format("gpx_%d.xml", i)), "    ");

			try (OutputStream fout = new FileOutputStream(new File(baseDir, format("gpx_%d.obj", i)));
				 ObjectOutputStream oout = new ObjectOutputStream(fout))
			{
				oout.writeObject(gpx);
			}
		}
	}

}
