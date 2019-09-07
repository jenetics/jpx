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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package io.jenetics.jpx.jdbc;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Email;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.GPX.Version;
import io.jenetics.jpx.GPXTest;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public class GPXAccessTest {

	private final Random random = new Random(1231321);
	private final GPX gpx = GPXTest.nextGPX(random);

	//@BeforeClass
	public void setup() throws IOException, SQLException {
		final String[] queries = IO.
			toSQLText(getClass().getResourceAsStream("/model-mysql.sql"))
			.split(";");

		MariaDB.INSTANCE.transaction(conn -> {
			for (String query : queries) {
				if (!query.trim().isEmpty()) {
					try (Statement stmt = conn.createStatement()) {
						stmt.execute(query);
					}
				}
			}
		});
	}

	//@Test
	public void list() throws IOException {
		final String dir = "/home/fwilhelm/Workspace/Documents/GPS/Split";

		Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(
				final Path file,
				final BasicFileAttributes attrs
			)
				throws IOException
			{
				if (!Files.isDirectory(file)) {
					System.out.println(file);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	//@Test
	public void insert() throws SQLException, IOException {
		final String dir = "/home/fwilhelm/Workspace/Documents/GPS";

		Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(
				final Path file,
				final BasicFileAttributes attrs
			)
				throws IOException
			{
				if (!Files.isDirectory(file) &&
					file.toString().endsWith(".gpx") &&
					!file.toString().contains("Raw"))
				{
					final GPX gpx = fix(GPX.reader(Version.V10, Mode.LENIENT).read(file));

					final Path export = Paths.get(
						"/home/fwilhelm/Downloads/gpx/",
						gpx.getMetadata().flatMap(Metadata::getName).orElse("null") + ".gpx"
					);

					GPX.writer("    ").write(gpx, export);

					System.out.println("Inserting: " + file);

//					long start = System.currentTimeMillis();
//					try {
//						MariaDB.INSTANCE.transaction(conn -> {
//							final Long id = GPXAccess.insert(gpx, conn);
//						});
//					} catch (SQLException e) {
//						throw new IOException(e);
//					}
//					long stop = System.currentTimeMillis();
//					System.out.println(format("%s: %s s", file, (stop - start)/1000.0));
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static GPX fix(final GPX gpx) {
		final Person author = Person.of(
			"Franz Wilhelmstötter",
			Email.of("franz.wilhelmstoetter@gmail.com")
		);
		final Copyright copyright = Copyright.of("Franz Wilhelmstötter");
		final Bounds bounds = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.collect(toBounds());

		final ZonedDateTime time = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.flatMap(wp -> wp.getTime().map(Stream::of).orElse(Stream.empty()))
			.min(Comparator.naturalOrder())
			.orElse(null);


		return gpx.toBuilder()
			.version(Version.V11)
			.creator("JPX - https://github.com/jenetics/jpx")
			.metadata(md -> md
				.name(format("tracks-%s", time != null ? time.toLocalDate() : null))
				.author(author)
				.copyright(copyright)
				.bounds(bounds)
				.time(time))
			.build();
	}

	private static Collector<WayPoint, ?, Bounds> toBounds() {
		return Collector.of(
			() -> {
				final double[] a = new double[4];
				a[0] = Double.MAX_VALUE;
				a[1] = Double.MAX_VALUE;
				a[2] = Double.MIN_VALUE;
				a[3] = Double.MIN_VALUE;
				return a;
			},
			(a, b) -> {
				a[0] = min(b.getLatitude().doubleValue(), a[0]);
				a[1] = min(b.getLongitude().doubleValue(), a[1]);
				a[2] = max(b.getLatitude().doubleValue(), a[2]);
				a[3] = max(b.getLongitude().doubleValue(), a[3]);
			},
			(a, b) -> {
				a[0] = min(a[0], b[0]);
				a[1] = min(a[1], b[1]);
				a[2] = max(a[2], b[2]);
				a[3] = max(a[3], b[3]);
				return a;
			},
			a -> Bounds.of(a[0], a[1], a[2], a[3])
		);
	}

}
