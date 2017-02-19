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
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Lists.immutable;
import static io.jenetics.jpx.Lists.mutable;
import static io.jenetics.jpx.Parsers.toMandatoryString;
import static io.jenetics.jpx.XMLReader.attr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * GPX documents contain a metadata header, followed by way-points, routes, and
 * tracks. You can add your own elements to the extensions section of the GPX
 * document.
 * <h3>Examples</h3>
 * <b>Creating a GPX object with one track-segment and 3 track-points</b>
 * <pre>{@code
 * final GPX gpx = GPX.builder()
 *     .addTrack(track -> track
 *         .addSegment(segment -> segment
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
 *     .build();
 * }</pre>
 *
 * <h4>Reading a GPX file</h4>
 * <pre>{@code
 * final GPX gpx = GPX.read("track.xml");
 * }</pre>
 *
 * <h4>Reading erroneous GPX files</h4>
 * <pre>{@code
 * final boolean lenient = true;
 * final GPX gpx = GPX.read("track.xml", lenient);
 * }</pre>
 *
 * This allows to read otherwise invalid GPX files, like
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/0">
 *     <metadata>
 *         <time>2015-11-13T15:22:42.140Z</time>
 *         <bounds minlat="-37050536.000000000" minlon="-0.000000000" maxlat="48.359161377" maxlon="16.448385239"/>
 *     </metadata>
 *     <trk>
 *         <name>track-1</name>
 *         <desc>Log every 3 sec, 0 m</desc>
 *         <trkseg>
 *             <trkpt></trkpt>
 *             <trkpt lat="48.199352264" lon="16.403341293">
 *                 <ele>4325376.000000</ele>
 *                 <time>2015-10-23T17:07:08Z</time>
 *                 <speed>2.650000</speed>
 *                 <name>TP000001</name>
 *             </trkpt>
 *             <trkpt lat="6.376383781" lon="-0.000000000">
 *                 <ele>147573952589676412928.000000</ele>
 *                 <time>1992-07-19T10:10:58Z</time>
 *                 <speed>464.010010</speed>
 *                 <name>TP000002</name>
 *             </trkpt>
 *             <trkpt lat="-37050536.000000000" lon="0.000475423">
 *                 <ele>0.000000</ele>
 *                 <time>2025-12-17T05:10:27Z</time>
 *                 <speed>56528.671875</speed>
 *                 <name>TP000003</name>
 *             </trkpt>
 *             <trkpt></trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 *
 * which is read as
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/0">
 *     <metadata>
 *         <time>2015-11-13T15:22:42.140Z</time>
 *     </metadata>
 *     <trk>
 *         <name>track-1</name>
 *         <desc>Log every 3 sec, 0 m</desc>
 *         <trkseg>
 *             <trkpt lat="48.199352264" lon="16.403341293">
 *                 <ele>4325376.000000</ele>
 *                 <time>2015-10-23T17:07:08Z</time>
 *                 <speed>2.650000</speed>
 *                 <name>TP000001</name>
 *             </trkpt>
 *             <trkpt lat="6.376383781" lon="-0.000000000">
 *                 <ele>147573952589676412928.000000</ele>
 *                 <time>1992-07-19T10:10:58Z</time>
 *                 <speed>464.010010</speed>
 *                 <name>TP000002</name>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.1
 * @since 1.0
 */
public final class GPX implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The default version number: 1.1.
	 */
	public static final String VERSION = "1.1";

	/**
	 * The default creator string.
	 */
	public static final String CREATOR = "JPX - https://jenetics.github.io/jpx";

	private final String _creator;
	private final String _version;
	private final Metadata _metadata;
	private final List<WayPoint> _wayPoints;;
	private final List<Route> _routes;
	private final List<Track> _tracks;

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param version the GPX version
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @throws NullPointerException if the {@code creator} or {@code version} is
	 *         {@code null}
	 */
	private GPX(
		final String version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		_version = requireNonNull(version);
		_creator = requireNonNull(creator);
		_metadata = metadata;
		_wayPoints = immutable(wayPoints);
		_routes = immutable(routes);
		_tracks = immutable(tracks);
	}

	/**
	 * Return the version number of the GPX file.
	 *
	 * @return the version number of the GPX file
	 */
	public String getVersion() {
		return _version;
	}

	/**
	 * Return the name or URL of the software that created your GPX document.
	 * This allows others to inform the creator of a GPX instance document that
	 * fails to validate.
	 *
	 * @return the name or URL of the software that created your GPX document
	 */
	public String getCreator() {
		return _creator;
	}

	/**
	 * Return the metadata of the GPX file.
	 *
	 * @return the metadata of the GPX file
	 */
	public Optional<Metadata> getMetadata() {
		return Optional.ofNullable(_metadata);
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} way-points.
	 *
	 * @return an unmodifiable list of the {@code GPX} way-points.
	 */
	public List<WayPoint> getWayPoints() {
		return _wayPoints;
	}

	/**
	 * Return a stream with all {@code WayPoint}s of this {@code GPX} object.
	 *
	 * @return a stream with all {@code WayPoint}s of this {@code GPX} object
	 */
	public Stream<WayPoint> wayPoints() {
		return _wayPoints.stream();
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} routes.
	 *
	 * @return an unmodifiable list of the {@code GPX} routes.
	 */
	public List<Route> getRoutes() {
		return _routes;
	}

	/**
	 * Return a stream of the {@code GPX} routes.
	 *
	 * @return a stream of the {@code GPX} routes.
	 */
	public Stream<Route> routes() {
		return _routes.stream();
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} tracks.
	 *
	 * @return an unmodifiable list of the {@code GPX} tracks.
	 */
	public List<Track> getTracks() {
		return _tracks;
	}

	/**
	 * Return a stream of the {@code GPX} tracks.
	 *
	 * @return a stream of the {@code GPX} tracks.
	 */
	public Stream<Track> tracks() {
		return _tracks.stream();
	}

	/**
	 * Convert the <em>immutable</em> GPX object into a <em>mutable</em>
	 * builder initialized with the current GPX values.
	 *
	 * @since !__version__!
	 *
	 * @return a new track builder initialized with the values of {@code this}
	 *         GPX object
	 */
	public Builder toBuilder() {
		return builder(_creator, _version)
			.metadata(_metadata)
			.wayPoints(_wayPoints)
			.routes(_routes)
			.tracks(_tracks);
	}

	@Override
	public String toString() {
		return format(
			"GPX[way-points=%s, routes=%s, tracks=%s]",
			getWayPoints().size(), getRoutes().size(), getTracks().size()
		);
	}

	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_creator) + 31;
		hash += 17*Objects.hashCode(_version) + 31;
		hash += 17*Objects.hashCode(_metadata) + 31;
		hash += 17*Objects.hashCode(_wayPoints) + 31;
		hash += 17*Objects.hashCode(_routes) + 31;
		hash += 17*Objects.hashCode(_tracks) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GPX &&
			Objects.equals(((GPX)obj)._creator, _creator) &&
			Objects.equals(((GPX)obj)._version, _version) &&
			Objects.equals(((GPX)obj)._metadata, _metadata) &&
			Objects.equals(((GPX)obj)._wayPoints, _wayPoints) &&
			Objects.equals(((GPX)obj)._routes, _routes) &&
			Objects.equals(((GPX)obj)._tracks, _tracks);
	}


	/**
	 * Builder class for creating immutable {@code GPX} objects.
	 * <p>
	 * Creating a GPX object with one track-segment and 3 track-points:
	 * <pre>{@code
	 * final GPX gpx = GPX.builder()
	 *     .addTrack(track -> track
	 *         .addSegment(segment -> segment
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder {
		private String _creator;
		private String _version;
		private Metadata _metadata;
		private List<WayPoint> _wayPoints = emptyList();
		private List<Route> _routes = emptyList();
		private List<Track> _tracks = emptyList();

		private Builder(final String version, final String creator) {
			_version = requireNonNull(version);
			_creator = requireNonNull(creator);
		}

		/**
		 * Set the GPX creator.
		 *
		 * @param creator the GPX creator
		 * @throws NullPointerException if the given argument is {@code null}
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder creator(final String creator) {
			_creator = requireNonNull(creator);
			return this;
		}

		/**
		 * Set the GPX metadata.
		 *
		 * @param metadata the GPX metadata
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder metadata(final Metadata metadata) {
			_metadata = metadata;
			return this;
		}

		/**
		 * Allows to set partial metadata without messing up with the
		 * {@link Metadata.Builder} class.
		 * <pre>{@code
		 * final GPX gpx = GPX.builder()
		 *     .metadata(md -> md.author("Franz Wilhelmstötter"))
		 *     .addTrack(...)
		 *     .build();
		 * }</pre>
		 *
		 * @param metadata the metadata consumer
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder metadata(final Consumer<Metadata.Builder> metadata) {
			final Metadata.Builder builder = Metadata.builder();
			metadata.accept(builder);
			_metadata = builder.build();

			return this;
		}

		/**
		 * Sets the way-points of the {@code GPX} object.
		 *
		 * @param wayPoints the {@code GPX} way-points
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder wayPoints(final List<WayPoint> wayPoints) {
			_wayPoints = wayPoints != null ? wayPoints : emptyList();
			return this;
		}

		/**
		 * Add one way-point to the {@code GPX} object.
		 *
		 * @param wayPoint the way-point to add
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code wayPoint} is
		 *         {@code null}
		 */
		public Builder addWayPoint(final WayPoint wayPoint) {
			_wayPoints = mutable(_wayPoints);
			_wayPoints.add(requireNonNull(wayPoint));

			return this;
		}

		/**
		 * Add a way-point to the {@code GPX} object using a
		 * {@link WayPoint.Builder}.
		 * <pre>{@code
		 * final GPX gpx = GPX.builder()
		 *     .addWayPoint(wp -> wp.lat(23.6).lon(13.5).ele(50))
		 *     .build();
		 * }</pre>
		 *
		 * @param wayPoint the way-point to add, configured by the way-point
		 *        builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addWayPoint(final Consumer<WayPoint.Builder> wayPoint) {
			final WayPoint.Builder builder = WayPoint.builder();
			wayPoint.accept(builder);
			return addWayPoint(builder.build());
		}

		/**
		 * Sets the routes of the {@code GPX} object.
		 *
		 * @param routes the {@code GPX} routes
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder routes(final List<Route> routes) {
			_routes = routes != null ? routes : emptyList();
			return this;
		}

		/**
		 * Add a route the {@code GPX} object.
		 *
		 * @param route the route to add
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code route} is {@code null}
		 */
		public Builder addRoute(final Route route) {
			_routes = mutable(_routes);
			_routes.add(requireNonNull(route));

			return this;
		}

		/**
		 * Add a route the {@code GPX} object.
		 * <pre>{@code
		 * final GPX gpx = GPX.builder()
		 *     .addRoute(route -> route
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161)))
		 *     .build();
		 * }</pre>
		 *
		 * @param route the route to add, configured by the route builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addRoute(final Consumer<Route.Builder> route) {
			final Route.Builder builder = Route.builder();
			route.accept(builder);
			return addRoute(builder.build());
		}

		/**
		 * Sets the tracks of the {@code GPX} object.
		 *
		 * @param tracks the {@code GPX} tracks
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder tracks(final List<Track> tracks) {
			_tracks = tracks != null ? tracks : emptyList();
			return this;
		}

		/**
		 * Add a track the {@code GPX} object.
		 *
		 * @param track the track to add
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code track} is {@code null}
		 */
		public Builder addTrack(final Track track) {
			_tracks = mutable(_tracks);
			_tracks.add(requireNonNull(track));

			return this;
		}

		/**
		 * Add a track the {@code GPX} object.
		 * <pre>{@code
		 * final GPX gpx = GPX.builder()
		 *     .addTrack(track -> track
		 *         .addSegment(segment -> segment
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
		 *     .build();
		 * }</pre>
		 *
		 * @param track the track to add, configured by the track builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addTrack(final Consumer<Track.Builder> track) {
			final Track.Builder builder = Track.builder();
			track.accept(builder);
			return addTrack(builder.build());
		}

		/**
		 * Create an immutable {@code GPX} object from the current builder state.
		 *
		 * @return an immutable {@code GPX} object from the current builder state
		 */
		public GPX build() {
			return new GPX(
				_version,
				_creator,
				_metadata,
				_wayPoints,
				_routes,
				_tracks
			);
		}

		public Filter<WayPoint, Builder> wayPointFilter() {
			return null;
		}

		public Filter<Route, Builder> routeFilter() {
			return null;
		}

		public Filter<Track, Builder> trackFilter() {
			return null;
		}

	}

	/**
	 * Create a new GPX builder with the given GPX version and creator string.
	 *
	 * @param version the GPX version string
	 * @param creator the GPX creator
	 * @return new GPX builder
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Builder builder(final String version, final String creator) {
		return new Builder(version, creator);
	}

	/**
	 * Create a new GPX builder with the given GPX creator string.
	 *
	 * @param creator the GPX creator
	 * @return new GPX builder
	 * @throws NullPointerException if the given arguments is {@code null}
	 */
	public static Builder builder(final String creator) {
		return builder(VERSION, creator);
	}

	/**
	 * Create a new GPX builder.
	 *
	 * @return new GPX builder
	 */
	public static Builder builder() {
		return builder(VERSION, CREATOR);
	}


	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @return a new {@code GPX} object with the given data
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	public static GPX of(
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		return new GPX(
			VERSION,
			creator,
			metadata,
			wayPoints,
			routes,
			tracks
		);
	}

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param  version the GPX version
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @return a new {@code GPX} object with the given data
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	public static GPX of(
		final String version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		return new GPX(
			version,
			creator,
			metadata,
			wayPoints,
			routes,
			tracks
		);
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	/**
	 * Writes this {@code Link} object to the given XML stream {@code writer}.
	 *
	 * @param writer the XML data sink
	 * @throws XMLStreamException if an error occurs
	 */
	void write(final XMLStreamWriter writer) throws XMLStreamException {
		final XMLWriter xml = new XMLWriter(writer);

		xml.write("gpx",
			xml.ns("http://www.topografix.com/GPX/1/1"),
			xml.attr("version", _version),
			xml.attr("creator", _creator),
			xml.elem(_metadata, Metadata::write),
			xml.elems(_wayPoints, (p, w) -> p.write("wpt", w)),
			xml.elems(_routes, Route::write),
			xml.elems(_tracks, Track::write)
		);
	}

	@SuppressWarnings("unchecked")
	static XMLReader<GPX> reader() {
		final XML.Function<Object[], GPX> creator = a -> GPX.of(
			toMandatoryString(a[0], "GPX.version"),
			toMandatoryString(a[1], "GPX.creator"),
			(Metadata)a[2],
			(List<WayPoint>)a[3],
			(List<Route>)a[4],
			(List<Track>)a[5]
		);

		return XMLReader.of(creator, "gpx",
			attr("version"),
			attr("creator"),
			Metadata.reader(),
			XMLReader.ofList(WayPoint.reader("wpt")),
			XMLReader.ofList(Route.reader()),
			XMLReader.ofList(Track.reader())
		);
	}

	/* *************************************************************************
	 *  Load GPX from file.
	 * ************************************************************************/

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param output the output stream where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final OutputStream output)
		throws IOException
	{
		write(gpx, output, null);
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final Path path) throws IOException {
		try (FileOutputStream out = new FileOutputStream(path.toFile());
			 BufferedOutputStream bout = new BufferedOutputStream(out))
		{
			write(gpx, bout);
		}
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final String path) throws IOException {
		write(gpx, Paths.get(path));
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param output the output stream where the GPX object is written to
	 * @param indent the indent string for pretty printing. If the string is
	 *        {@code null}, no pretty printing is performed.
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(
		final GPX gpx,
		final OutputStream output,
		final String indent
	)
		throws IOException
	{
		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		try {
			final XMLStreamWriter writer = indent != null
				? new IndentingXMLWriter(
					factory.createXMLStreamWriter(output), indent)
				: factory.createXMLStreamWriter(output);

			writer.writeStartDocument("UTF-8", "1.0");
			gpx.write(writer);
			writer.writeEndDocument();
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @param indent the indent string for pretty printing. If the string is
	 *        {@code null}, no pretty printing is performed.
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final Path path, final String indent)
		throws IOException
	{
		try (FileOutputStream out = new FileOutputStream(path.toFile());
			 BufferedOutputStream bout = new BufferedOutputStream(out))
		{
			write(gpx, bout, indent);
		}
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @param indent the indent string for pretty printing. If the string is
	 *        {@code null}, no pretty printing is performed.
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final String path, final String indent)
		throws IOException
	{
		write(gpx, Paths.get(path), indent);
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param input the input stream from where the GPX date is read
	 * @param lenient if {@code true}, out-of-range and syntactical errors are
	 *        ignored. E.g. a {@code WayPoint} with {@code lat} values not in
	 *        the valid range of [-90..90] are ignored/skipped.
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final InputStream input, final boolean lenient)
		throws IOException
	{
		final XMLInputFactory factory = XMLInputFactory.newFactory();
		try {
			final XMLStreamReader reader = factory.createXMLStreamReader(input);
			if (reader.hasNext()) {
				reader.next();
				return reader().read(reader, lenient);
			} else {
				throw new IOException("No 'gpx' element found.");
			}
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param input the input stream from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final InputStream input) throws IOException {
		return read(input, false);
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param path the input path from where the GPX date is read
	 * @param lenient if {@code true}, out-of-range and syntactical errors are
	 *        ignored. E.g. a {@code WayPoint} with {@code lat} values not in
	 *        the valid range of [-90..90] are ignored/skipped.
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final Path path, final boolean lenient)
		throws IOException
	{
		try (FileInputStream in = new FileInputStream(path.toFile());
			 BufferedInputStream bin = new BufferedInputStream(in))
		{
			return read(bin, lenient);
		}
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param path the input path from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final Path path) throws IOException {
		return read(path, false);
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param path the input path from where the GPX date is read
	 * @param lenient if {@code true}, out-of-range and syntactical errors are
	 *        ignored. E.g. a {@code WayPoint} with {@code lat} values not in
	 *        the valid range of [-90..90] are ignored/skipped.
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final String path, final boolean lenient)
		throws IOException
	{
		return read(Paths.get(path), lenient);
	}

	/**
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @param path the input path from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final String path) throws IOException {
		return read(Paths.get(path), false);
	}

}
