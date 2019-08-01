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
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.GPX.Version;
import io.jenetics.jpx.Length.Unit;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 */
public class GPXTest extends XMLStreamTestBase<GPX> {

@Test
public void extensions() throws ParserConfigurationException {
	final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	final DocumentBuilder db = dbf.newDocumentBuilder();
	final Document doc = db.newDocument();

	GPX gpx = GPX.builder()
		.extensions(doc)
		.build();
}

	@Test
	public void foo1() throws ParserConfigurationException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element ext = doc.createElement("extesions");
		Element elem = doc.createElement( "property");
		elem.setAttribute("name", "my_attribute");
		elem.setTextContent("Some Attribute Value");

		ext.appendChild(elem);
		doc.appendChild(ext);

		GPX gpx = GPX.builder()
			.extensions(doc)
			.build();

		GPX.writer("    ").write(gpx, System.out);
	}

	@Override
	public Supplier<GPX> factory(Random random) {
		return () -> nextGPX(random);
	}

	@Override
	protected Params<GPX> params(final Version version, final Random random) {
		return new Params<>(
			() -> nextGPX(random),
			GPX.xmlReader(version),
			GPX.xmlWriter(version)
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
		return XML.parse("<extensions xmlns=\"http://www.topografix.com/GPX/1/1\">\n" +
			"\t<gpxdata:lap xmlns:gpxdata=\"http://www.cluetrust.com/XML/GPXDATA/1/0\">\n" +
			"\t\t<gpxdata:index>1</gpxdata:index>\n" +
			"\t\t<gpxdata:startPoint lat=\"51.219983\" lon=\"6.765224\"/>\n" +
			"\t\t<gpxdata:endPoint lat=\"51.220137\" lon=\"6.765098\" />\n" +
			"\t\t<gpxdata:startTime>2009-06-19T10:13:04Z</gpxdata:startTime>\n" +
			"\t\t<gpxdata:elapsedTime>4.6700000</gpxdata:elapsedTime>\n" +
			"\t\t<gpxdata:calories>1</gpxdata:calories>\n" +
			"\t\t<gpxdata:distance>0.5881348</gpxdata:distance>\n" +
			"\t\t<gpxdata:summary name=\"AverageHeartRateBpm\" kind=\"avg\">163</gpxdata:summary>\n" +
			"\t\t<gpxdata:trigger kind=\"manual\" />\n" +
			"\t\t<gpxdata:intensity>active</gpxdata:intensity>\n" +
			"\t</gpxdata:lap>\n" +
			"</extensions>");
	}

	//@Test
	public void foo() {
		final Document doc1 = doc();
		final Document doc2 = doc();
		System.out.println(doc1.equals(doc2));
		System.out.println(doc1.hashCode() + ":" + doc2.hashCode());
		System.out.println(doc1.isEqualNode(doc2));
	}

	//@Test
	public void print() throws IOException {
		final GPX gpx = nextGPX(new Random(6123)).toBuilder()
			.version(Version.V10)
			.build();

		GPX.writer("    ").write(gpx, System.out);
	}

	@Test(invocationCount = 5)
	public void toStringFromString() {
		final GPX expected = nextGPX(new Random());
		final String string = GPX.writer("  ").toString(expected);
		//System.out.println(string);
		final GPX actual = GPX.reader().fromString(string);

		Assert.assertEquals(actual, expected);
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
	public void ignoreExtensions() throws IOException {
		final String resource = "/io/jenetics/jpx/extensions-gpx.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
		}

		final String[] names = gpx.wayPoints()
			.map(WayPoint::getName)
			.map(Optional::get)
			.toArray(String[]::new);

		Assert.assertEquals(names, new String[]{"Wien", "Eferding", "Freistadt", "Gmunden"});
	}

	@Test
	public void lenientRead() throws IOException {
		final String resource = "/io/jenetics/jpx/invalid-latlon.xml";
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx = GPX.reader(Mode.LENIENT).read(in);

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
			GPX.read(in);
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

	@Test(invocationCount = 10)
	public void readWriteRandomIndentedGPX() throws IOException {
		final Random random = new Random();
		final GPX gpx = nextGPX(random);

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GPX.writer("    ").write(gpx, bout);

		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		final GPX read = GPX.read(bin);

		Assert.assertEquals(read, gpx);
	}

	@Test(invocationCount = 10)
	public void readWriteRandomNonIndentedGPX() throws IOException {
		final Random random = new Random();
		final GPX gpx = nextGPX(random);

		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GPX.writer().write(gpx, bout);

		final ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
		final GPX read = GPX.read(bin);


		if (!read.equals(gpx)) {
			System.out.println(new String(bout.toByteArray()));
		}
		Assert.assertEquals(read, gpx);
	}

	@Test(dataProvider = "readWriteGPX")
	public void readWrite(final String resource) throws IOException {
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			final GPX gpx1 = GPX.read(in);

			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			GPX.writer("    ").write(gpx1, out);
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

	@Test
	public void equalsVerifier() {
		EqualsVerifier.forClass(GPX.class)
			.withIgnoredFields("_extensions")
			.verify();
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
				GPX.writer("    ")
					.write(read, Paths.get(baseDir, format("gpx_%d(1).xml", i)));
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
			.map(wp -> wp.getCourse().orElseThrow(IllegalArgumentException::new))
			.collect(Collectors.toList());

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
			.time(ZonedDateTimeFormat.parse("2016-08-21T12:24:27Z"))
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
			.time(ZonedDateTimeFormat.parse("2016-08-21T12:24:27Z"))
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
			.time(ZonedDateTimeFormat.parse("2016-08-21T12:24:27Z"))
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
			.time(ZonedDateTimeFormat.parse("2016-08-21T12:24:27Z"))
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
			.time(ZonedDateTimeFormat.parse("2016-08-21T12:24:27Z"))
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
			return GPX.reader(Version.V10, Mode.STRICT).read(in);
		}
	}

	private GPX readV11(final String name, final Mode mode) throws IOException {
		final String resource = "/io/jenetics/jpx/" + name;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			return GPX.reader(Version.V11, mode).read(in);
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
			gpx.getExtensions().get().getDocumentElement()
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
			gpx.getExtensions().get()
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

			GPX.writer("    ").write(gpx, Paths.get(baseDir, format("gpx_%d.xml", i)));
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
			gpx = GPX.reader(Mode.LENIENT).read(in);
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
	public void issue77_XMLComment() throws IOException {
		final String resource = "/io/jenetics/jpx/ISSUE-77.gpx";

		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
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
			gpx = GPX.read(in);
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
			gpx = GPX.read(in);
		}

		final long count = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.count();
		Assert.assertEquals(count, 479L);

		final long extensions = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.flatMap(p -> p.getExtensions()
				.map(Stream::of)
				.orElse(Stream.empty()))
			.count();
		Assert.assertEquals(extensions, 479L);
	}

	@Test
	public void issue86_Parsing() throws IOException {
		String resource = "/io/jenetics/jpx/GPX-full.gpx";
		final GPX expected;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			expected = GPX.read(in);
		}

		resource = "/io/jenetics/jpx/ISSUE-86.gpx";
		final GPX gpx;
		try (InputStream in = getClass().getResourceAsStream(resource)) {
			gpx = GPX.read(in);
		}

		Assert.assertEquals(gpx, expected);
	}

}
