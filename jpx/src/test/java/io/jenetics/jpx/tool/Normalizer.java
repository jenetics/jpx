package io.jenetics.jpx.tool;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static io.jenetics.jpx.Bounds.toBounds;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Email;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

public class Normalizer {

	public static void main(final String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("GPX <file>");
		//	System.exit(1);
		}

		//final Path file = Paths.get(args[0]).toAbsolutePath();
		final Path file = Paths.get("/home/fwilhelm/Downloads/Ultra GPS Logger/2019-01-20_144714_Vienna.nmea.gpx");
		System.out.println("Splitting " + file);

		final GPX gpx = GPX
			.reader(GPX.Version.V11, GPX.Reader.Mode.LENIENT)
			.read(file);

		final GPX normalized = normalize(gpx);
		final Path f = Paths.get(
			file.getParent().toString(),
			fileName(normalized) + ".gpx"
		);

		GPX.writer("    ").write(normalized, f);
	}

	private static GPX normalize(final GPX gpx) {
		final Track track = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.filter(PointFilter.FAULTY_POINTS)
			.collect(Tracks.toTrack(Duration.ofMinutes(5), 10));

		return normalizeMetadata(
			gpx.toBuilder()
				.tracks(singletonList(track))
				.build()
		);
	}

	private static GPX normalizeMetadata(final GPX gpx) {
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
			.version(GPX.Version.V11)
			.creator("JPX - Java GPX library")
			.metadata(md -> md
				.name(format("track-%s", time != null ? time.toLocalDate() : null))
				.author(author)
				.copyright(copyright)
				.bounds(bounds)
				.time(time))
			.build();
	}

	private static String fileName(final GPX gpx)  {
		return gpx.getMetadata()
			.flatMap(Metadata::getTime)
			.map(ZonedDateTime::toOffsetDateTime)
			.map(Objects::toString)
			.orElse("" + System.currentTimeMillis());
	}

}
