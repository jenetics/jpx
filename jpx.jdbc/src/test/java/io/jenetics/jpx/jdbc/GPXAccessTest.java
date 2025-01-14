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
import java.util.stream.Stream;

import org.testng.annotations.Test;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Email;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Reader.Mode;
import io.jenetics.jpx.GPX.Version;
import io.jenetics.jpx.GPXTest;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

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
			toSQLText(getClass().getResourceAsStream("/model-psql.sql"))
			.split(";");

		PSQLDB.INSTANCE.transaction(conn -> {
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

//					final Path export = Paths.get(
//						"/home/fwilhelm/Downloads/gpx/",
//						gpx.getMetadata().flatMap(Metadata::getName).orElse("null") + ".gpx"
//					);
//
//					GPX.writer("    ").write(gpx, export);

					System.out.println("Inserting: " + file);

					long start = System.currentTimeMillis();
					try {
						PSQLDB.INSTANCE.transaction(conn -> {
							final Long id = GPXAccess.insert(gpx, conn);
						});
					} catch (SQLException e) {
						throw new IOException(e);
					}
					long stop = System.currentTimeMillis();
					System.out.println(format("%s: %s s", file, (stop - start)/1000.0));
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
			.collect(Bounds.toBounds());

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

}
