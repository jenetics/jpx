package io.jenetics.jpx.tool;

import static java.time.ZoneId.systemDefault;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static io.jenetics.jpx.Bounds.toBounds;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.jenetics.jpx.Bounds;
import io.jenetics.jpx.Copyright;
import io.jenetics.jpx.Email;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.GPX.Writer.Indent;
import io.jenetics.jpx.Metadata;
import io.jenetics.jpx.Person;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;
import io.jenetics.jpx.WayPoint;

public class Normalizer {

	private static final
	Collector<WayPoint, ?, Map<LocalDate, List<WayPoint>>>
	TRACK_GROUPS = groupingBy(
		p -> p.getTime()
			.map(i -> LocalDate.ofInstant(i, UTC))
			.orElse(LocalDate.MIN)
	);

	public static void main(final String[] args) throws Exception {
		final Path gpxDir = Paths.get("/home/fwilhelm/Downloads/2021/");
		final Path outputDir = Paths.get("/home/fwilhelm/Downloads/normalized/");

		Files.walkFileTree(gpxDir, new SimpleFileVisitor<>() {
			@Override
			public FileVisitResult visitFile(
				final Path file,
				final BasicFileAttributes attrs
			)
				throws IOException
			{
				if (!Files.isDirectory(file) && file.toString().endsWith(".gpx")) {
					normalize(file, outputDir);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private static void normalize(final Path file, final Path dir) throws IOException {
		System.out.println("Normalizing: " + file);

		final GPX gpx = GPX.Reader.of(GPX.Version.V11, GPX.Reader.Mode.LENIENT)
			.read(file);

		final Map<LocalDate, List<WayPoint>> split = split(gpx);

		final List<GPX> normalized = split.values().stream()
			.flatMap(Normalizer::errorFilter)
			.flatMap(points -> toGPX(points).stream())
			.collect(Collectors.toList());

		write(dir, normalized);
	}

	private static void write(final Path dir, final List<GPX> gpxs)
		throws IOException
	{
		for (GPX gpx : gpxs) {
			final Instant time = gpx.getMetadata()
				.flatMap(Metadata::getTime)
				.orElse(Instant.MAX);

			final Path file = Paths.get(
				dir.toString(),
				String.valueOf(LocalDate.ofInstant(time, UTC).getYear()),
				fileName(gpx)
			);
			if (!Files.exists(file.getParent())) {
				Files.createDirectories(file.getParent() );
			}

			System.out.println("Writing " + file);

			GPX.Writer.of(new Indent("    ")).write(gpx, file);
			setFileTime(file, LocalDateTime.ofInstant(time, systemDefault()));
			//writeNative(file, gpx);
		}
	}

	private static void writeNative(final Path file, final Object gpx) throws IOException {
		final Path f = Paths.get(file.toString() + ".bin");

		try (FileOutputStream bout = new FileOutputStream(f.toFile());
			 ObjectOutputStream oout = new ObjectOutputStream(bout))
		{
			oout.writeObject(gpx);
		}
	}

	private static void setFileTime(final Path path, final LocalDateTime time)
		throws IOException
	{
		final BasicFileAttributeView attr = Files.getFileAttributeView(
			path,
			BasicFileAttributeView.class
		);
		final FileTime ft = FileTime.fromMillis(
			time.toInstant(UTC).toEpochMilli()
		);
		attr.setTimes(ft, ft, ft);
	}

	private static Map<LocalDate, List<WayPoint>> split(final GPX gpx) {
		return gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.collect(TRACK_GROUPS);
	}

	private static Stream<List<WayPoint>> errorFilter(final List<WayPoint> points) {
		final List<WayPoint> filtered = points.stream()
			.filter(PointFilter.FAULTY_POINTS)
			.collect(Collectors.toList());

		return filtered.size() >= 10
			? Stream.of(filtered)
			: Stream.empty();
	}

	private static Optional<GPX> toGPX(final List<WayPoint> points) {
		final Optional<Track> track = points.stream()
			.collect(Tracks.toTrack(Duration.ofMinutes(5), 10));

		return track.map(t -> normalizeMetadata(
			GPX.builder()
				.tracks(singletonList(t))
				.build()
		));
	}

	private static GPX normalizeMetadata(final GPX gpx) {
		final Instant time = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.flatMap(wp -> wp.getTime().stream())
			.min(Comparator.naturalOrder())
			.orElse(Instant.now());

		final Person author = Person.of(
			"Franz Wilhelmstötter",
			Email.of("franz.wilhelmstoetter@gmail.com")
		);

		final Copyright copyright = Copyright.of(
			"Franz Wilhelmstötter",
			LocalDate.ofInstant(time, UTC).getYear()
		);

		final Bounds bounds = gpx.tracks()
			.flatMap(Track::segments)
			.flatMap(TrackSegment::points)
			.collect(toBounds());

		final String name = LocalDate.ofInstant(time, UTC) + ".gpx";

		return gpx.toBuilder()
			.version(GPX.Version.V11)
			.metadata(md -> md
				.name(name)
				.author(author)
				.copyright(copyright)
				.bounds(bounds)
				.time(time))
			.build();
	}

	private static String fileName(final GPX gpx)  {
		return gpx.getMetadata()
			.flatMap(Metadata::getTime)
			.map(i -> LocalDate.ofInstant(i, UTC))
			.map(Objects::toString)
			.orElse("" + System.currentTimeMillis()) + ".gpx";
	}

}
