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
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static io.jenetics.jpx.ListsTest.revert;

import nl.jqno.equalsverifier.EqualsVerifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.GPX.Version;
import io.jenetics.jpx.GPX.Writer.Indent;
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
	protected Params<GPX> params(final Version version, final Random random) {
		final var format = NumberFormat.getNumberInstance(ENGLISH);
		final Function<String, Length> lengthParser = string ->
			Length.parse(string, format);

		return new Params<>(
			() -> nextGPX(random),
			GPX.xmlReader(version, lengthParser),
			GPX.xmlWriter(version, Formats::format)
		);
	}

	static GPX nextGPX(final Random random) {
		return GPX.of(
			Version.V11,
			format("creator_%s", random.nextInt(100)),
			random.nextBoolean() ? MetadataTest.nextMetadata(random) : null,
			random.nextBoolean() ? WayPointTest.nextWayPoints(random) : null,
			random.nextBoolean() ? RouteTest.nextRoutes(random) : null,
			random.nextBoolean() ? TrackTest.nextTracks(random) : null,
			random.nextBoolean() ? doc() : null
		);
	}

	static Document doc() {
		return XML.parse("""
			<extensions xmlns="http://www.topografix.com/GPX/1/1">
			    <gpxdata:lap xmlns:gpxdata="http://www.cluetrust.com/XML/GPXDATA/1/0">
			        <gpxdata:index>1</gpxdata:index>
			        <gpxdata:startPoint lat="51.219983" lon="6.765224"/>
			        <gpxdata:endPoint lat="51.220137" lon="6.765098" />
			        <gpxdata:startTime>2009-06-19T10:13:04Z</gpxdata:startTime>
			        <gpxdata:elapsedTime>4.6700000</gpxdata:elapsedTime>
			        <gpxdata:calories>1</gpxdata:calories>
			        <gpxdata:distance>0.5881348</gpxdata:distance>
			        <gpxdata:summary name="AverageHeartRateBpm" kind="avg">163</gpxdata:summary>
			        <gpxdata:trigger kind="manual" />
			        <gpxdata:intensity>active</gpxdata:intensity>
			    </gpxdata:lap>
			</extensions>
			""");
	}

	@Test
	public void writeToDocument() throws Exception {
		final var gpx = nextGPX(new Random());

		final var doc = XMLProvider.provider()
			.documentBuilderFactory()
			.newDocumentBuilder()
			.newDocument();

		// The GPX data are written to the empty `doc` object.
		GPX.Writer.of(Indent.NULL, 20).write(gpx, new DOMResult(doc));

		final var xmlString = XML.toString(doc);
		final var gpx2 = GPX.Reader.DEFAULT.fromString(xmlString);

		assertThat(gpx2).isEqualTo(gpx);
	}

	@Test
	public void readFromDocument() throws Exception {
		final var gpx = nextGPX(new Random());

		final var doc = XMLProvider.provider()
			.documentBuilderFactory()
			.newDocumentBuilder()
			.newDocument();

		// The GPX data are written to the empty `doc` object.
		GPX.Writer
			.of(Indent.NULL, 20)
			.write(gpx, new DOMResult(doc));

		//final var gpx2 = GPX.Reader.DEFAULT.read(new DOMSource(doc));
		//assertThat(gpx2).isEqualTo(gpx);

		final var out = new ByteArrayOutputStream();
		TransformerFactory.newInstance().newTransformer()
			.transform(new DOMSource(doc), new StreamResult(out));

		final String xml = out.toString();
		final GPX gpx2 = GPX.Reader.DEFAULT.fromString(xml);
		assertThat(gpx2).isEqualTo(gpx);
	}

	//@Test
	public void print() throws IOException {
		final GPX gpx = nextGPX(new Random(6123)).toBuilder()
			.version(Version.V10)
			.build();

		GPX.Writer.of(new Indent("    ")).write(gpx, System.out);
	}

	@Test(invocationCount = 5)
	public void toStringFromString() {
		final GPX expected = nextGPX(new Random());
		final String string = GPX.Writer.of(new Indent("    "), 25).toString(expected);
		//System.out.println(string);
		final GPX actual = GPX.Reader.DEFAULT.fromString(string);

		Assert.assertEquals(actual, expected);
	}

	@Test(dataProvider = "validEmptyElementsFiles")
	public void validEmptyElements(final String resource, final GPX expected)
		throws IOException
	{
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.Reader.DEFAULT.read(in);
			assertThat(gpx).isEqualTo(expected);
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
			GPX.Reader.DEFAULT.read(in);
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
	public void ignoreExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-gpx.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		final String[] names = gpx.wayPoints()
			.map(WayPoint::getName)
			.map(Optional::orElseThrow)
			.toArray(String[]::new);

		Assert.assertEquals(names, new String[]{"Wien", "Eferding", "Freistadt", "Gmunden"});
	}

	@Test
	public void lenientRead() throws IOException {
		final String resource = "/io/jenetics/jpx/invalid-latlon.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.Reader.of(Mode.LENIENT).read(in);

			Assert.assertTrue(gpx.getMetadata().isPresent());
			Assert.assertFalse(gpx.getMetadata().get().getBounds().isPresent());

			final int length = (int)gpx.tracks()
				.flatMap(Track::segments)
				.flatMap(TrackSegment::points)
				.count();

			Assert.assertEquals(length, 4);
		}
	}

	@Test(expectedExceptions = {InvalidObjectException.class})
	public void strictRead() throws IOException, InvalidObjectException {
		final String resource = "/io/jenetics/jpx/invalid-latlon.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			GPX.Reader.DEFAULT.read(in);
		}
	}

	@Test
	public void loadFullSampleFile() throws IOException, XMLStreamException {
		final String rsc = "/io/jenetics/jpx/Gpx-full-sample.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(rsc)) {
			gpx = GPX.Reader.DEFAULT.read(in);
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
			.orElseThrow();

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
			Optional.of(TimeFormat.parse("2009-05-19T04:00:30Z"))
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
			gpx = GPX.Reader.DEFAULT.read(in);
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
		Assert.assertNotEquals(
			System.identityHashCode(gpx.toBuilder().build()),
			System.identityHashCode(gpx)
		);
	}

	@Test(invocationCount = 10)
	public void readWriteRandomIndentedGPX() throws IOException {
		final Random random = new Random();
		final GPX gpx = nextGPX(random);

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GPX.Writer.of(new Indent("    "), 20).write(gpx, bout);

		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		final GPX read = GPX.Reader.DEFAULT.read(bin);

		Assert.assertEquals(read, gpx);
	}

	@Test(invocationCount = 10)
	public void readWriteRandomNonIndentedGPX() throws IOException {
		final var random = new Random();
		final GPX gpx = nextGPX(random);

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GPX.Writer.of(Indent.NULL, 20).write(gpx, bout);

		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		final GPX read = GPX.Reader.DEFAULT.read(bin);


		//if (!read.equals(gpx)) {
		//	System.out.println(bout);
		//}
		Assert.assertEquals(read, gpx);
	}

	@Test(dataProvider = "readWriteGPX")
	public void readWrite(final String resource) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx1 = GPX.Reader.DEFAULT.read(in);

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			GPX.Writer.of(new Indent("    ")).write(gpx1, out);
			final GPX gpx2 = GPX.Reader.DEFAULT.read(new ByteArrayInputStream(out.toByteArray()));

			Assert.assertEquals(gpx1, gpx2);
		}
	}

	@DataProvider(name = "readWriteGPX")
	public Object[][] readWriteGPX() {
		return new Object[][] {
			{"/io/jenetics/jpx/ISSUE-38.gpx.xml"}
		};
	}

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(GPX.class)
			.withIgnoredFields("_extensions")
			.verify();
	}

	@Test(invocationCount = 10)
	public void writeToByteArray() {
		final GPX gpx = nextGPX(new Random());
		final byte[] bytes = GPX.Writer.DEFAULT.toByteArray(gpx);

		assertThat(GPX.Reader.DEFAULT.formByteArray(bytes)).isEqualTo(gpx);
	}

	@Test(invocationCount = 5)
	public void serialize() throws IOException, ClassNotFoundException {
		final GPX object = nextGPX(new Random());
		Serialization.test(object);
	}

	@Test
	public void compatibleSerialization() throws IOException, ClassNotFoundException {
		final String baseDir = "src/test/resources/io/jenetics/jpx/serialization";

		final var random = new Random(123);
		for (int i = 0; i < 15; ++i) {
			final GPX gpx = nextGPX(random);

			/*
			final var fout = Files.newOutputStream(
				Paths.get(baseDir, format("gpx_%d.obj", i)),
				StandardOpenOption.TRUNCATE_EXISTING
			);
			try (fout; var oout = new ObjectOutputStream(fout)) {
				oout.writeObject(gpx);
			}
			final var writer = GPX.Writer.of(new Indent("    "), 50);
			writer.write(gpx, (Paths.get(baseDir, format("gpx_%d.xml", i))));
			 */

			final GPX read = GPX.read(Paths.get(baseDir, format("gpx_%d.xml", i)));
			try {
				Assert.assertEquals(read, gpx);
			} catch (AssertionError e) {
				GPX.Writer.of(new Indent("    "))
					.write(read, Paths.get(baseDir, format("gpx_%d(1).xml", i)));
				throw e;
			}


			try (InputStream fin = new FileInputStream(new File(baseDir, format("gpx_%d.obj", i)));
				 ObjectInputStream oin = new ObjectInputStream(fin))
			{
				Assert.assertEquals(oin.readObject(), gpx);
			}
		}
	}

	@Test
	public void readCourse() throws IOException {
		final GPX gpx = readV10("GPX_10-1.gpx");

		final List<Degrees> courses = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.filter(wp -> wp.getCourse().isPresent())
			.map(wp -> wp.getCourse().orElseThrow())
			.toList();

		Assert.assertEquals(
			courses,
			asList(Degrees.ofDegrees(341.6), Degrees.ofDegrees(298.6))
		);
	}

	@Test
	public void readGPXv10_1() throws IOException {
		final Metadata expected = Metadata.builder()
			.name("Five Hikes in the White Mountains")
			.desc("Five Hikes in the White Mountains!!")
			.author(Person.of(
				"Franz Wilhelmstötter",
				Email.of("franz.wilhelmstoetter@gmail.com"),
				Link.of(
					"https://github.com/jenetics/jpx",
					"Visit my New Hampshire hiking website!",
					null)))
			.time(TimeFormat.parse("2016-08-21T12:24:27Z"))
			.keywords("Hiking, NH, Presidential Range")
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		final GPX gpx = readV10("GPX_10-1.gpx");
		Assert.assertEquals(gpx.getMetadata(), Optional.of(expected));
	}

	@Test
	public void readGPXv10_2() throws IOException {
		final Metadata expected = Metadata.builder()
			.desc("Five Hikes in the White Mountains!!")
			.author(Person.of(
				"Franz Wilhelmstötter",
				Email.of("franz.wilhelmstoetter@gmail.com"),
				Link.of(
					"https://github.com/jenetics/jpx",
					"Visit my New Hampshire hiking website!",
					null)))
			.time(TimeFormat.parse("2016-08-21T12:24:27Z"))
			.keywords("Hiking, NH, Presidential Range")
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		Assert.assertEquals(readV10("GPX_10-2.gpx").getMetadata(), Optional.of(expected));
	}

	@Test
	public void readGPXv10_3() throws IOException {
		final Metadata expected = Metadata.builder()
			.author(Person.of(
				"Franz Wilhelmstötter",
				Email.of("franz.wilhelmstoetter@gmail.com"),
				Link.of(
					"https://github.com/jenetics/jpx",
					"Visit my New Hampshire hiking website!",
					null)))
			.time(TimeFormat.parse("2016-08-21T12:24:27Z"))
			.keywords("Hiking, NH, Presidential Range")
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		Assert.assertEquals(readV10("GPX_10-3.gpx").getMetadata(), Optional.of(expected));
	}

	@Test
	public void readGPXv10_4() throws IOException {
		final Metadata expected = Metadata.builder()
			.author(Person.of(
				null,
				Email.of("franz.wilhelmstoetter@gmail.com"),
				Link.of(
					"https://github.com/jenetics/jpx",
					"Visit my New Hampshire hiking website!",
					null)))
			.time(TimeFormat.parse("2016-08-21T12:24:27Z"))
			.keywords("Hiking, NH, Presidential Range")
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		Assert.assertEquals(readV10("GPX_10-4.gpx").getMetadata(), Optional.of(expected));
	}

	@Test
	public void readGPXv10_5() throws IOException {
		final Metadata expected = Metadata.builder()
			.author(Person.of(
				null,
				null,
				Link.of(
					"https://github.com/jenetics/jpx",
					"Visit my New Hampshire hiking website!",
					null)))
			.time(TimeFormat.parse("2016-08-21T12:24:27Z"))
			.keywords("Hiking, NH, Presidential Range")
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		Assert.assertEquals(readV10("GPX_10-5.gpx").getMetadata(), Optional.of(expected));
	}

	@Test
	public void readGPXv10_6() throws IOException {
		final Metadata expected = Metadata.builder()
			.bounds(Bounds.of(42.1, 71.9, 42.4, 71.1))
			.build();

		Assert.assertEquals(readV10("GPX_10-6.gpx").getMetadata(), Optional.of(expected));
	}

	private GPX readV10(final String name) throws IOException {
		final String resource = "/io/jenetics/jpx/" + name;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			return GPX.Reader.of(Version.V10, Mode.STRICT).read(in);
		}
	}

	private GPX readV11(final String name, final Mode mode) throws IOException {
		final String resource = "/io/jenetics/jpx/" + name;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			return GPX.Reader.of(Version.V11, mode).read(in);
		}
	}

	private GPX readV11(final String name) throws IOException {
		return readV11(name, Mode.STRICT);
	}

	@Test
	public void readGPXExtensions() throws IOException {
		final GPX gpx = readV11("GPX_extensions.gpx");
		final Document expected = doc();
		//System.out.println(XML.toString(expected.getDocumentElement()));
		//System.out.println(XML.toString(gpx.getExtensions().get().getDocumentElement()));
		//GPX.writer("    ").write(gpx, System.out);
		Assert.assertTrue(XML.equals(
			expected.getDocumentElement(),
			gpx.getExtensions().orElseThrow().getDocumentElement()
		));
	}

	@Test
	public void readGPXExtensions2() throws IOException {
		final GPX gpx = readV11("extensions.gpx");
		//GPX.writer("    ").write(gpx, System.out);
	}

	@Test(expectedExceptions = IOException.class)
	public void readInvalidGPXExtensions1() throws IOException {
		final GPX gpx = readV11("GPX_invalid_extensions.gpx");
	}

	@Test
	public void readEmptyGPXExtensions() throws IOException {
		final GPX gpx = readV11("GPX_empty_extensions.gpx");
		Assert.assertEquals(gpx.getExtensions(), Optional.empty());
	}

	@Test
	public void extensionsRoot() {
		final Document extensions = XML.parse("<extensions>some test</extensions>");
		final GPX gpx = GPX.builder()
			.extensions(extensions)
			.build();

		Assert.assertTrue(XML.equals(
			XML.removeNS(extensions),
			gpx.getExtensions().orElseThrow()
		));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidExtensionsRoot1() {
		final Document extensions = XML.parse("<extensions xmlns=\"adsf\">some test</extensions>");
		final GPX gpx = GPX.builder()
			.extensions(extensions)
			.build();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void invalidExtensionsRoot2() {
		final Document extensions = XML.parse("<foo>some test</foo>");
		GPX.builder()
			.extensions(extensions);
	}

	public static void main1(final String[] args) throws IOException {
		final String baseDir = "jpx/src/test/resources/io/jenetics/jpx/serialization";

		final Random random = new Random(123);
		for (int i = 0; i < 15; ++i) {
			final GPX gpx = nextGPX(random);

			GPX.Writer.of(new Indent("    ")).write(gpx, Paths.get(baseDir, format("gpx_%d.xml", i)));
			try (OutputStream fout = new FileOutputStream(new File(baseDir, format("gpx_%d.obj", i)));
				 ObjectOutputStream oout = new ObjectOutputStream(fout))
			{
				oout.writeObject(gpx);
			}
		}
	}



	/* *************************************************************************
	 * Testing specific issues.
	 * ************************************************************************/

	@Test(timeOut = 2000)
	public void issue49_NPEForInvalidGPX() {
		final String resource = "/io/jenetics/jpx/ISSUE-49.gpx";

		try (InputStream in = getClass().getResourceAsStream(resource)) {
			GPX.Reader.DEFAULT.read(in);
			Assert.fail("GXP.read must throw.");
		} catch (IOException e) {
			Assert.assertEquals(e.getClass(), InvalidObjectException.class);
			Assert.assertTrue(e.getMessage().toLowerCase().contains("invalid"));
		}
	}

	@Test(timeOut = 2000)
	public void issue49_NPEForInvalidGPXLenient() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-49.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.of(Mode.LENIENT).read(in);
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
			.toList();

		Assert.assertEquals(points.size(), 26);
	}

	@Test(timeOut = 2000)
	public void issue51_XMLComment() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-51.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		Assert.assertTrue(gpx.getMetadata().isPresent());
		final Metadata md = gpx.getMetadata().get();
		Assert.assertFalse(md.getName().isPresent());
		Assert.assertTrue(md.getLinks().isEmpty());
		Assert.assertEquals(gpx.getWayPoints().size(), 3);
	}

	@Test
	public void issue77_XMLComment() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-77.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}
		Assert.assertEquals(
			gpx.getTracks().get(0).getName().orElse(null),
			"09-OKT-18 15:12:08"
		);
	}

	@Test
	public void issue78_Parsing() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-78.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}
		Assert.assertEquals(
			gpx.getWayPoints().get(0).getName().orElse(null),
			"Wien"
		);
	}

	@Test
	public void issue82_Parsing() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-82.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		final long count = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.count();
		Assert.assertEquals(count, 479L);

		final long extensions = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.flatMap(p -> p.getExtensions().stream())
			.count();
		Assert.assertEquals(extensions, 479L);
	}

	@Test
	public void issue86_Parsing() throws IOException {
		String resource = "/io/jenetics/jpx/GPX-full.gpx";
		final GPX expected;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			expected = GPX.Reader.DEFAULT.read(in);
		}

		resource = "/io/jenetics/jpx/ISSUE-86.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		Assert.assertEquals(gpx, expected);
	}

	@Test
	public void issue151_Formatting() throws IOException {
		final var resource = "/io/jenetics/jpx/ISSUE-151.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.DEFAULT.read(in);
		}

		final var out = new ByteArrayOutputStream();
		GPX.Writer.of(new Indent("    ")).write(gpx, out);
		//GPX.writer(new Indent("    ")).write(gpx, System.out);
		assertThat(out.toString()).doesNotContain("-3.1E-4");
	}

	@Test
	public void issue162_NumberFormattingParsing() {
		final var gpx = GPX.builder()
			.addWayPoint(wp -> wp.ele(1234.5).build(1.2, 3.4))
			.build();

		final var string = GPX.Writer.DEFAULT.toString(gpx);
		assertThat(string).isEqualToIgnoringNewLines("""
			<?xml version="1.0" encoding="UTF-8"?>
			<gpx version="1.1" creator="JPX - https://github.com/jenetics/jpx" \
			xmlns="http://www.topografix.com/GPX/1/1">
			    <wpt lat="1.2" lon="3.4">
			        <ele>1234.5</ele>
			    </wpt>
			</gpx>"""
		);

		final var gpx2 = GPX.Reader.DEFAULT.fromString(string);
		assertThat(gpx2).isEqualTo(gpx);
	}

	@Test
	public void issue170_InvalidGPXVersion() throws IOException {
		final var resource = "/io/jenetics/jpx/ISSUE-170.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.Reader.of(Mode.LENIENT).read(in);
		}

		assertThat(gpx.getVersion()).isEqualTo("1.1");
		assertThat(gpx.getCreator()).isEqualTo("Zepp App");
		assertThat(gpx.getTracks()).hasSize(1);
		assertThat(gpx.getTracks().get(0).getName().orElseThrow())
			.isEqualTo("20230507 mile iles");
		assertThat(gpx.getTracks().get(0).getSegments()).hasSize(1);
		assertThat(gpx.getTracks().get(0).getSegments().get(0)).hasSize(2);
	}

}
