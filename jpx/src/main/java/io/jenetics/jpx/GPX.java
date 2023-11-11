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
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Locale.ENGLISH;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Lists.copyOf;
import static io.jenetics.jpx.Lists.copyTo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serial;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

/**
 * GPX documents contain a metadata header, followed by way-points, routes, and
 * tracks. You can add your own elements to the extensions section of the GPX
 * document.
 * <p>
 * <em><b>Examples:</b></em>
 * <p>
 * <b>Creating a GPX object with one track-segment and 3 track-points</b>
 * {@snippet lang="java":
 * final GPX gpx = GPX.builder()
 *     .addTrack(track -> track
 *         .addSegment(segment -> segment
 *             .addPoint(p -> p.lat(48.20100).lon(16.31651).ele(283))
 *             .addPoint(p -> p.lat(48.20112).lon(16.31639).ele(278))
 *             .addPoint(p -> p.lat(48.20126).lon(16.31601).ele(274))))
 *     .build();
 * }
 *
 * <b>Writing a GPX file</b>
 * {@snippet lang="java":
 * final var indent = new GPX.Writer.Indent("    ");
 * GPX.Writer.of(indent).write(gpx, Path.of("points.gpx"));
 * }
 *
 * This will produce the following output.
 * {@snippet lang="java":
 * <gpx version="1.1" creator="JPX - https://github.com/jenetics/jpx" xmlns="http://www.topografix.com/GPX/1/1">
 *     <trk>
 *         <trkseg>
 *             <trkpt lat="48.201" lon="16.31651">
 *                 <ele>283</ele>
 *             </trkpt>
 *             <trkpt lat="48.20112" lon="16.31639">
 *                 <ele>278</ele>
 *             </trkpt>
 *             <trkpt lat="48.20126" lon="16.31601">
 *                 <ele>274</ele>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }
 *
 * <b>Reading a GPX file</b>
 * {@snippet lang="java":
 * final GPX gpx = GPX.read("points.xml");
 * }
 *
 * <b>Reading erroneous GPX files</b>
 * {@snippet lang="java":
 * final GPX gpx = GPX.Reader.of(GPX.Reader.Mode.LENIENT).read("track.xml");
 * }
 *
 * This allows to read otherwise invalid GPX files, like
 * {@snippet lang="java":
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/1">
 *   <metadata>
 *     <time>2019-12-31T21:36:04.134Z</time>
 *     <bounds minlat="48.175186667" minlon="16.299580000" maxlat="48.199555000" maxlon="16.416933333"/>
 *   </metadata>
 *   <trk>
 *     <trkseg>
 *       <trkpt lat="48.184298333" lon="16.299580000">
 *         <ele>0.000</ele>
 *         <time>2011-03-20T09:47:16Z</time>
 *         <geoidheight>43.5</geoidheight>
 *         <fix>2d</fix>
 *         <sat>3</sat>
 *         <hdop>4.200000</hdop>
 *         <vdop>1.000000</vdop>
 *         <pdop>4.300000</pdop>
 *       </trkpt>
 *       <trkpt lat="48.175186667" lon="16.303916667">
 *         <ele>0.000</ele>
 *         <time>2011-03-20T09:51:31Z</time>
 *         <geoidheight>43.5</geoidheight>
 *         <fix>2d</fix>
 *         <sat>3</sat>
 *         <hdop>16.600000</hdop>
 *         <vdop>0.900000</vdop>
 *         <pdop>16.600000</pdop>
 *       </trkpt>
 *     </trkseg>
 *   </trk>
 * </gpx>
 * }
 *
 * which is read as (if you write it again)
 * {@snippet lang="java":
 * <?xml version="1.0" encoding="UTF-8"?>
 * <gpx version="1.1" creator="GPSBabel - http://www.gpsbabel.org" xmlns="http://www.topografix.com/GPX/1/1">
 *     <metadata>
 *         <time>2019-12-31T21:36:04.134Z</time>
 *         <bounds minlat="48.175187" minlon="16.29958" maxlat="48.199555" maxlon="16.416933"></bounds>
 *     </metadata>
 *     <trk>
 *         <trkseg>
 *             <trkpt lat="48.184298" lon="16.29958">
 *                 <ele>0</ele>
 *                 <time>2011-03-20T09:47:16Z</time>
 *                 <geoidheight>43.5</geoidheight>
 *                 <fix>2d</fix>
 *                 <sat>3</sat>
 *                 <hdop>4.2</hdop>
 *                 <vdop>1</vdop>
 *                 <pdop>4.3</pdop>
 *             </trkpt>
 *             <trkpt lat="48.175187" lon="16.303917">
 *                 <ele>0</ele>
 *                 <time>2011-03-20T09:51:31Z</time>
 *                 <geoidheight>43.5</geoidheight>
 *                 <fix>2d</fix>
 *                 <sat>3</sat>
 *                 <hdop>16.6</hdop>
 *                 <vdop>0.9</vdop>
 *                 <pdop>16.6</pdop>
 *             </trkpt>
 *         </trkseg>
 *     </trk>
 * </gpx>
 * }
 *
 * <b>Converting a GPX object to an XML {@link Document}</b>
 * {@snippet lang="java":
 * final GPX gpx = ...;
 *
 * final Document doc = XMLProvider.provider()
 *     .documentBuilderFactory()
 *     .newDocumentBuilder()
 *     .newDocument();
 *
 * // The GPX data are written to the empty `doc` object.
 * GPX.Writer.DEFAULT.write(gpx, new DOMResult(doc));
 * }
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 2.0
 * @since 1.0
 */
public final class GPX implements Serializable {

	@Serial
	private static final long serialVersionUID = 2L;

	/**
	 * Represents the available GPX versions.
	 *
	 * @version 1.3
	 * @since 1.3
	 */
	public enum Version {

		/**
		 * The GPX version 1.0. This version can be read and written.
		 *
		 * @see <a href="http://www.topografix.com/gpx_manual.asp">GPX 1.0</a>
		 */
		V10("1.0", "http://www.topografix.com/GPX/1/0"),

		/**
		 * The GPX version 1.1. This is the default version and can be read and
		 * written.
		 *
		 * @see <a href="http://www.topografix.com/GPX/1/1">GPX 1.1</a>
		 */
		V11("1.1", "http://www.topografix.com/GPX/1/1");

		private final String _value;
		private final String _namespaceURI;

		Version(final String value, final String namespaceURI) {
			_value = value;
			_namespaceURI = namespaceURI;
		}

		/**
		 * Return the version string value.
		 *
		 * @return the version string value
		 */
		public String getValue() {
			return _value;
		}

		/**
		 * Return the namespace URI of this version.
		 *
		 * @since 1.5
		 *
		 * @return the namespace URI of this version
		 */
		public String getNamespaceURI() {
			return _namespaceURI;
		}

		/**
		 * Return the version from the given {@code version} string. Allowed
		 * values are "1.0" and "1.1".
		 *
		 * @param version the version string
		 * @return the version from the given {@code version} string
		 * @throws IllegalArgumentException if the given {@code version} string
		 *         is neither "1.0" nor "1.1"
		 * @throws NullPointerException if the given {@code version} string is
		 *         {@code null}
		 */
		public static Version of(final String version) {
			return switch (version) {
				case "1.0" -> V10;
				case "1.1" -> V11;
				default -> throw new IllegalArgumentException(format(
					"Unknown version string: '%s'.", version
				));
			};
		}
	}

	private static final String _CREATOR = "JPX - https://github.com/jenetics/jpx";

	private final String _creator;
	private final Version _version;
	private final Metadata _metadata;
	private final List<WayPoint> _wayPoints;
	private final List<Route> _routes;
	private final List<Track> _tracks;
	private final Document _extensions;

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
	 * @param extensions the XML extensions document
	 * @throws NullPointerException if the {@code creator} or {@code version} is
	 *         {@code null}
	 */
	private GPX(
		final Version version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks,
		final Document extensions
	) {
		_version = requireNonNull(version);
		_creator = requireNonNull(creator);
		_metadata = metadata;
		_wayPoints = copyOf(wayPoints);
		_routes = copyOf(routes);
		_tracks = copyOf(tracks);
		_extensions = extensions;
	}

	/**
	 * Return the version number of the GPX file.
	 *
	 * @return the version number of the GPX file
	 */
	public String getVersion() {
		return _version._value;
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
	 * Return the (cloned) extensions document. The root element of the returned
	 * document has the name {@code extensions}.
	 * {@snippet lang="java":
	 * <extensions>
	 *     ...
	 * </extensions>
	 * }
	 *
	 * @since 1.5
	 *
	 * @return the extensions document
	 */
	public Optional<Document> getExtensions() {
		return Optional.ofNullable(_extensions).map(XML::clone);
	}

	/**
	 * Convert the <em>immutable</em> GPX object into a <em>mutable</em>
	 * builder initialized with the current GPX values.
	 *
	 * @since 1.1
	 *
	 * @return a new track builder initialized with the values of {@code this}
	 *         GPX object
	 */
	public Builder toBuilder() {
		return builder(_version, _creator)
			.metadata(_metadata)
			.wayPoints(_wayPoints)
			.routes(_routes)
			.tracks(_tracks)
			.extensions(_extensions);
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
		return hash(
			_creator,
			_version,
			_metadata,
			_wayPoints,
			_routes,
			_tracks
		);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof GPX gpx &&
			Objects.equals(gpx._creator, _creator) &&
			Objects.equals(gpx._version, _version) &&
			Objects.equals(gpx._metadata, _metadata) &&
			Objects.equals(gpx._wayPoints, _wayPoints) &&
			Objects.equals(gpx._routes, _routes) &&
			Objects.equals(gpx._tracks, _tracks);
	}

	/**
	 * Builder class for creating immutable {@code GPX} objects.
	 * <p>
	 * Creating a GPX object with one track-segment and 3 track-points:
	 * {@snippet lang="java":
	 * final GPX gpx = GPX.builder()
	 *     .addTrack(track -> track
	 *         .addSegment(segment -> segment
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
	 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
	 *     .build();
	 * }
	 */
	public static final class Builder {
		private String _creator;
		private Version _version;
		private Metadata _metadata;
		private final List<WayPoint> _wayPoints = new ArrayList<>();
		private final List<Route> _routes = new ArrayList<>();
		private final List<Track> _tracks = new ArrayList<>();
		private Document _extensions;

		private Builder(final Version version, final String creator) {
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
		 * Return the current creator value.
		 *
		 * @since 1.1
		 *
		 * @return the current creator value
		 */
		public String creator() {
			return _creator;
		}

		/**
		 * Set the GPX version.
		 *
		 * @since 1.3
		 *
		 * @param version the GPX version
		 * @throws NullPointerException if the given argument is {@code null}
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder version(final Version version) {
			_version = requireNonNull(version);
			return this;
		}

		/**
		 * Return the current version value.
		 *
		 * @since 1.1
		 *
		 * @return the current version value
		 */
		public String version() {
			return _version._value;
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
		 * Allows setting partial metadata without messing up with the
		 * {@link Metadata.Builder} class.
		 * {@snippet lang="java":
		 * final GPX gpx = GPX.builder()
		 *     .metadata(md -> md.author("Franz Wilhelmstötter"))
		 *     .addTrack(...)
		 *     .build();
		 * }
		 *
		 * @param metadata the metadata consumer
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder metadata(final Consumer<? super Metadata.Builder> metadata) {
			final Metadata.Builder builder = Metadata.builder();
			metadata.accept(builder);

			final Metadata md = builder.build();
			_metadata = md.isEmpty() ? null : md;

			return this;
		}

		/**
		 * Return the current metadata value.
		 *
		 * @since 1.1
		 *
		 * @return the current metadata value
		 */
		public Optional<Metadata> metadata() {
			return Optional.ofNullable(_metadata);
		}

		/**
		 * Sets the way-points of the {@code GPX} object. The list of way-points
		 * may be {@code null}.
		 *
		 * @param wayPoints the {@code GPX} way-points
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the way-points in the list is
		 *         {@code null}
		 */
		public Builder wayPoints(final List<WayPoint> wayPoints) {
			copyTo(wayPoints, _wayPoints);
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
			_wayPoints.add(requireNonNull(wayPoint));
			return this;
		}

		/**
		 * Add a way-point to the {@code GPX} object using a
		 * {@link WayPoint.Builder}.
		 * {@snippet lang="java":
		 * final GPX gpx = GPX.builder()
		 *     .addWayPoint(wp -> wp.lat(23.6).lon(13.5).ele(50))
		 *     .build();
		 * }
		 *
		 * @param wayPoint the way-point to add, configured by the way-point
		 *        builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addWayPoint(final Consumer<? super WayPoint.Builder> wayPoint) {
			final WayPoint.Builder builder = WayPoint.builder();
			wayPoint.accept(builder);
			return addWayPoint(builder.build());
		}

		/**
		 * Return the current way-points. The returned list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current, mutable way-point list
		 */
		public List<WayPoint> wayPoints() {
			return new NonNullList<>(_wayPoints);
		}

		/**
		 * Sets the routes of the {@code GPX} object. The list of routes may be
		 * {@code null}.
		 *
		 * @param routes the {@code GPX} routes
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the routes is {@code null}
		 */
		public Builder routes(final List<Route> routes) {
			copyTo(routes, _routes);
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
			_routes.add(requireNonNull(route));
			return this;
		}

		/**
		 * Add a route the {@code GPX} object.
		 * {@snippet lang="java":
		 * final GPX gpx = GPX.builder()
		 *     .addRoute(route -> route
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161)))
		 *     .build();
		 * }
		 *
		 * @param route the route to add, configured by the route builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addRoute(final Consumer<? super Route.Builder> route) {
			final Route.Builder builder = Route.builder();
			route.accept(builder);
			return addRoute(builder.build());
		}

		/**
		 * Return the current routes. The returned list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current, mutable route list
		 */
		public List<Route> routes() {
			return new NonNullList<>(_routes);
		}

		/**
		 * Sets the tracks of the {@code GPX} object. The list of tracks may be
		 * {@code null}.
		 *
		 * @param tracks the {@code GPX} tracks
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the tracks is {@code null}
		 */
		public Builder tracks(final List<Track> tracks) {
			copyTo(tracks, _tracks);
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
			_tracks.add(requireNonNull(track));
			return this;
		}

		/**
		 * Add a track the {@code GPX} object.
		 * {@snippet lang="java":
		 * final GPX gpx = GPX.builder()
		 *     .addTrack(track -> track
		 *         .addSegment(segment -> segment
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
		 *             .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
		 *     .build();
		 * }
		 *
		 * @param track the track to add, configured by the track builder
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addTrack(final Consumer<? super Track.Builder> track) {
			final Track.Builder builder = Track.builder();
			track.accept(builder);
			return addTrack(builder.build());
		}

		/**
		 * Return the current tracks. The returned list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current, mutable track list
		 */
		public List<Track> tracks() {
			return new NonNullList<>(_tracks);
		}


		/**
		 * Sets the extensions object, which may be {@code null}. The root
		 * element of the extensions document must be {@code extensions}.
		 * {@snippet lang="java":
		 * <extensions>
		 *     ...
		 * </extensions>
		 * }
		 *
		 * @since 1.5
		 *
		 * @param extensions the extensions document
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the root element is not the
		 *         an {@code extensions} node
		 */
		public Builder extensions(final Document extensions) {
			_extensions = XML.checkExtensions(extensions);
			return this;
		}

		/**
		 * Return the current extensions
		 *
		 * @since 1.5
		 *
		 * @return the extensions document
		 */
		public Optional<Document> extensions() {
			return Optional.ofNullable(_extensions);
		}

		/**
		 * Create an immutable {@code GPX} object from the current builder state.
		 *
		 * @return an immutable {@code GPX} object from the current builder state
		 */
		public GPX build() {
			return of(
				_version,
				_creator,
				_metadata,
				_wayPoints,
				_routes,
				_tracks,
				_extensions
			);
		}

		/**
		 * Return a new {@link WayPoint} filter.
		 * {@snippet lang="java":
		 * final GPX filtered = gpx.toBuilder()
		 *     .wayPointFilter()
		 *         .filter(wp -> wp.getTime().isPresent())
		 *         .build())
		 *     .build();
		 * }
		 *
		 * @since 1.1
		 *
		 * @return a new {@link WayPoint} filter
		 */
		public Filter<WayPoint, Builder> wayPointFilter() {
			return new Filter<>() {
				@Override
				public Filter<WayPoint, Builder> filter(
					final Predicate<? super WayPoint> predicate
				) {
					wayPoints(_wayPoints.stream().filter(predicate).toList());
					return this;
				}

				@Override
				public Filter<WayPoint, Builder> map(
					final Function<? super WayPoint, ? extends WayPoint> mapper
				) {
					wayPoints(
						_wayPoints.stream()
							.map(mapper)
							.map(WayPoint.class::cast)
							.toList()
					);

					return this;
				}

				@Override
				public Filter<WayPoint, Builder> flatMap(
					final Function<
						? super WayPoint,
						? extends List<WayPoint>> mapper
				) {
					wayPoints(
						_wayPoints.stream()
							.flatMap(wp -> mapper.apply(wp).stream())
							.toList()
					);

					return this;
				}

				@Override
				public Filter<WayPoint, Builder> listMap(
					final Function<
						? super List<WayPoint>,
						? extends List<WayPoint>> mapper
				) {
					wayPoints(mapper.apply(_wayPoints));

					return this;
				}

				@Override
				public Builder build() {
					return GPX.Builder.this;
				}

			};
		}

		/**
		 * Return a new {@link Route} filter.
		 * {@snippet lang="java":
		 * final GPX filtered = gpx.toBuilder()
		 *     .routeFilter()
		 *         .filter(Route::nonEmpty)
		 *         .build())
		 *     .build();
		 * }
		 *
		 * @since 1.1
		 *
		 * @return a new {@link Route} filter
		 */
		public Filter<Route, Builder> routeFilter() {
			return new Filter<>() {
				@Override
				public Filter<Route, Builder> filter(
					final Predicate<? super Route> predicate
				) {
					routes(
						_routes.stream()
							.filter(predicate)
							.toList()
					);

					return this;
				}

				@Override
				public Filter<Route, Builder> map(
					final Function<? super Route, ? extends Route> mapper
				) {
					routes(
						_routes.stream()
							.map(mapper)
							.map(Route.class::cast)
							.toList()
					);

					return this;
				}

				@Override
				public Filter<Route, Builder> flatMap(
					final Function<? super Route, ? extends List<Route>> mapper)
				{
					routes(
						_routes.stream()
							.flatMap(route -> mapper.apply(route).stream())
							.toList()
					);

					return this;
				}

				@Override
				public Filter<Route, Builder> listMap(
					final Function<
						? super List<Route>,
						? extends List<Route>> mapper
				) {
					routes(mapper.apply(_routes));

					return this;
				}

				@Override
				public Builder build() {
					return GPX.Builder.this;
				}

			};
		}

		/**
		 * Return a new {@link Track} filter.
		 * {@snippet lang="java":
		 * final GPX merged = gpx.toBuilder()
		 *     .trackFilter()
		 *         .map(track -> track.toBuilder()
		 *             .listMap(Filters::mergeSegments)
		 *             .filter(TrackSegment::nonEmpty)
		 *             .build())
		 *         .build()
		 *     .build();
		 * }
		 *
		 * @since 1.1
		 *
		 * @return a new {@link Track} filter
		 */
		public Filter<Track, Builder> trackFilter() {
			return new Filter<>() {
				@Override
				public Filter<Track, Builder> filter(
					final Predicate<? super Track> predicate
				) {
					tracks(_tracks.stream().filter(predicate).toList());
					return this;
				}

				@Override
				public Filter<Track, Builder> map(
					final Function<? super Track, ? extends Track> mapper
				) {
					tracks(
						_tracks.stream()
							.map(mapper)
							.map(Track.class::cast)
							.toList()
					);

					return this;
				}

				@Override
				public Filter<Track, Builder> flatMap(
					final Function<? super Track, ? extends List<Track>> mapper
				) {
					tracks(
						_tracks.stream()
							.flatMap(track -> mapper.apply(track).stream())
							.toList()
					);

					return this;
				}

				@Override
				public Filter<Track, Builder> listMap(
					final Function<
						? super List<Track>,
						? extends List<Track>> mapper
				) {
					tracks(mapper.apply(_tracks));

					return this;
				}

				@Override
				public Builder build() {
					return GPX.Builder.this;
				}

			};
		}

	}

	/**
	 * Create a new GPX builder with the given GPX version and creator string.
	 *
	 * @since 1.3
	 *
	 * @param version the GPX version
	 * @param creator the GPX creator
	 * @return new GPX builder
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Builder builder(final Version version, final String creator) {
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
		return builder(Version.V11, creator);
	}

	/**
	 * Create a new GPX builder.
	 *
	 * @return new GPX builder
	 */
	public static Builder builder() {
		return builder(Version.V11, _CREATOR);
	}


	/**
	 * Class for reading GPX files. A reader instance can be created by the
	 * {@code GPX.reader} factory methods.
	 *
	 * @version 3.0
	 * @since 1.3
	 */
	public static final class Reader {

		/**
		 * The possible GPX reader modes.
		 *
		 * @version 1.3
		 * @since 1.3
		 */
		public enum Mode {

			/**
			 * In this mode the GPX reader tries to ignore invalid GPX values
			 * and elements.
			 */
			LENIENT,

			/**
			 * Expects to read valid GPX files.
			 */
			STRICT
		}

		/**
		 * The <em>default </em>GPX reader, reading GPX files (v1.1) with
		 * reading mode {@link Mode#STRICT}.
		 *
		 * @since 3.0
		 */
		public static final Reader DEFAULT =  Reader.of(Version.V11, Mode.STRICT);

		private final Version _version;
		private final Mode _mode;

		private Reader(final Version version, final Mode mode) {
			_version = requireNonNull(version);
			_mode = requireNonNull(mode);
		}

		/**
		 * Return the GPX version {@code this} reader is able to read.
		 *
		 * @return the GPX version of {@code this} reader
		 */
		public Version version() {
			return _version;
		}

		/**
		 * Return the current reader mode.
		 *
		 * @return the current reader mode
		 */
		public Mode mode() {
			return _mode;
		}

		/**
		 * Read a GPX object from the given input {@code source}. This is the
		 * most general method for reading a {@code GPX} object.
		 *
		 * @since 3.0
		 *
		 * @param source the input source from where the GPX date is read
		 * @return the GPX object read from the source
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code source} is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 * @throws UnsupportedOperationException if the defined
		 *         {@link javax.xml.stream.XMLInputFactory} doesn't support
		 *         the given {@code source}
		 */
		public GPX read(final Source source)
			throws IOException
		{
			try {
				final XMLStreamReader reader = XMLProvider.provider()
					.xmlInputFactory()
					.createXMLStreamReader(source);

				try (var input = new XMLStreamReaderAdapter(reader)) {
					if (input.hasNext()) {
						input.next();

						final var format = NumberFormat.getNumberInstance(ENGLISH);
						final Function<String, Length> lengthParser = string ->
							Length.parse(string, format);

						return GPX.xmlReader(_version, lengthParser)
							.read(input, _mode == Mode.LENIENT);
					} else {
						throw new InvalidObjectException("No 'gpx' element found.");
					}
				} catch (XMLStreamException e) {
					throw new InvalidObjectException(
						"Invalid GPX: " + e.getMessage()
					);
				} catch (IllegalArgumentException e) {
					final var ioe = new InvalidObjectException(e.getMessage());
					throw (InvalidObjectException)ioe.initCause(e);
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		/**
		 * Read a GPX object from the given {@code input} stream.
		 *
		 * @param input the input stream from where the GPX date is read
		 * @return the GPX object read from the in stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 */
		public GPX read(final InputStream input)
			throws IOException
		{
			final var wrapper = new NonCloseableInputStream(input);
			try (var reader = new InputStreamReader(wrapper, UTF_8)) {
				return read(new StreamSource(reader));
			}
		}

		/**
		 * Read a GPX object from the given {@code path}.
		 *
		 * @param path the input path from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 */
		public GPX read(final Path path) throws IOException {
			try (var input = Files.newInputStream(path)) {
				return read(input);
			}
		}

		/**
		 * Read a GPX object from the given {@code file}.
		 *
		 * @param file the input file from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 */
		public GPX read(final File file) throws IOException {
			return read(file.toPath());
		}

		/**
		 * Read a GPX object from the given {@code path}.
		 *
		 * @param path the input path from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 */
		public GPX read(final String path) throws IOException {
			return read(Paths.get(path));
		}

		/**
		 * Create a GPX object from the given GPX-XML string.
		 *
		 * @see GPX.Writer#toString(GPX)
		 *
		 * @param xml the GPX XML string
		 * @return the GPX object created from the given XML string
		 * @throws IllegalArgumentException if the given {@code xml} is not a
		 *         valid GPX XML string
		 * @throws NullPointerException if the given {@code xml} string is
		 *         {@code null}
		 */
		public GPX fromString(final String xml) {
			try {
				return read(new ByteArrayInputStream(xml.getBytes()));
			} catch (InvalidObjectException e) {
				if (e.getCause() instanceof IllegalArgumentException iae) {
					throw iae;
				} else {
					throw new IllegalArgumentException(e);
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * Create a GPX object from the given {@code byte[]} array.
		 *
		 * @see GPX.Writer#toByteArray(GPX)
		 *
		 * @param bytes the GPX {@code byte[]} array
		 * @param offset the offset in the buffer of the first byte to read.
		 * @param length the maximum number of bytes to read from the buffer.
		 * @return the GPX object created from the given {@code byte[]} array
		 * @throws IllegalArgumentException if the given {@code byte[]} array
		 *         doesn't represent a valid GPX object
		 * @throws NullPointerException if the given {@code bytes} is {@code null}
		 */
		GPX formByteArray(
			final byte[] bytes,
			final int offset,
			final int length
		) {
			final var in = new ByteArrayInputStream(bytes, offset,  length);
			try (var din = new DataInputStream(in)) {
				return GPX.read(din);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}

		/**
		 * Create a GPX object from the given {@code byte[]} array.
		 *
		 * @see GPX.Writer#toByteArray(GPX)
		 *
		 * @param bytes the GPX {@code byte[]} array
		 * @return the GPX object created from the given {@code byte[]} array
		 * @throws IllegalArgumentException if the given {@code byte[]} array
		 *         doesn't represent a valid GPX object
		 * @throws NullPointerException if the given {@code bytes} is {@code null}
		 */
		GPX formByteArray(final byte[] bytes) {
			return formByteArray(bytes, 0, bytes.length);
		}

		/* *********************************************************************
		 * Factory methods.
		 * ********************************************************************/

		/**
		 * Return a GPX reader, reading GPX files with the given version and in the
		 * given reading mode.
		 *
		 * @since 3.0
		 *
		 * @param version the GPX version to read
		 * @param mode the reading mode
		 * @return a new GPX reader object
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static Reader of(final Version version, final Mode mode) {
			return new Reader(version, mode);
		}

		/**
		 * Return a GPX reader, reading GPX files with version 1.1 and in the given
		 * reading mode.
		 *
		 * @since 3.0
		 *
		 * @param mode the reading mode
		 * @return a new GPX reader object
		 * @throws NullPointerException if one of the arguments is {@code null}
		 */
		public static Reader of(final Mode mode) {
			return new Reader(Version.V11, mode);
		}

	}

	/**
	 * Class for writing GPX files. A writer instance can be created by the
	 * {@code GPX.writer} factory methods.
	 *
	 * @version 3.0
	 * @since 1.3
	 */
	public static final class Writer {

		/**
		 * Represents the indentation value, the writer is using. An indentation
		 * string of {@code null} means that the GPX data is written as one XML
		 * line. An empty string adds line feeds, but with no indentation.
		 *
		 * @since 3.0
		 *
		 * @param value the indentation value
		 */
		public record Indent(String value) {
			/**
			 * This indentation lets the {@link Writer} write the GPX data into
			 * one XML line.
			 */
			public static final Indent NULL = new Indent(null);

			/**
			 * No indentation, but with new-lines.
			 */
			public static final Indent NONE = new Indent("");

			/**
			 * Indentation with 4 spaces.
			 */
			public static final Indent SPACE4 = new Indent("    ");

			/**
			 * Indentation with 2 spaces.
			 */
			public static final Indent SPACE2 = new Indent("  ");

			/**
			 * Indentation with tabs.
			 */
			public static final Indent TAB1 = new Indent("\t");
		}

		/**
		 * The default value for the <em>maximum fraction digits</em>.
		 */
		public static final int DEFAULT_FRACTION_DIGITS = 8;

		/**
		 * The default GPX writer, with no indention and fraction digits
		 * of {@link #DEFAULT_FRACTION_DIGITS}.
		 *
		 * @see #of(Indent, int)
		 * @see #of(Indent)
		 *
		 * @since 3.0
		 */
		public static final Writer DEFAULT =
			new Writer(Indent.SPACE4, DEFAULT_FRACTION_DIGITS);

		private final Indent _indent;
		private final int _maximumFractionDigits;

		private Writer(final Indent indent, final int maximumFractionDigits) {
			_indent = requireNonNull(indent);
			_maximumFractionDigits = maximumFractionDigits;
		}

		/**
		 * Return the indentation string this GPX writer is using.
		 *
		 * @since 3.0
		 *
		 * @return the indentation string
		 */
		public Indent indent() {
			return _indent;
		}

		/**
		 * Return the maximum number of digits allowed in the fraction portion
		 * of the written numbers like <em>latitude</em> and <em>longitude</em>.
		 *
		 * @return the maximum number of digits allowed in the fraction portion
		 * 		   of the written numbers
		 */
		public int maximumFractionDigits() {
			return _maximumFractionDigits;
		}

		/**
		 * Writes the given {@code gpx} object to the given {@code result}. This
		 * is the most general way for writing {@link GPX} objects.
		 * <p>
		 * The following example shows how to create an XML-Document from a
		 * given {@code GPX} object.
		 * {@snippet lang="java":
		 * final GPX gpx = ...;
		 *
		 * final Document doc = XMLProvider.provider()
		 *     .documentBuilderFactory()
		 *     .newDocumentBuilder()
		 *     .newDocument();
		 *
		 * // The GPX data are written to the empty `doc` object.
		 * GPX.Writer.DEFAULT.write(gpx, new DOMResult(doc));
		 * }
		 *
		 * @since 3.0
		 *
		 * @param gpx the GPX object to write to the output
		 * @param result the output <em>document</em>
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final Result result)
			throws IOException
		{
			try {
				final XMLStreamWriter writer = XMLProvider.provider()
					.xmlOutputFactory()
					.createXMLStreamWriter(result);

				final XMLStreamWriterAdapter output = _indent.value() == null
					? new XMLStreamWriterAdapter(writer)
					: new IndentingXMLStreamWriter(writer, _indent.value());

				try (output) {
					final var format = NumberFormat.getNumberInstance(ENGLISH);
					format.setMaximumFractionDigits(_maximumFractionDigits);
					format.setGroupingUsed(false);
					final Function<Number, String> formatter = value ->
						value != null ? format.format(value) : null;

					output.writeStartDocument("UTF-8", "1.0");
					GPX.xmlWriter(gpx._version, formatter).write(output, gpx);
					output.writeEndDocument();
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code output} stream. <em>The caller of this method is responsible
		 * for closing the given {@code output} stream.</em>
		 *
		 * @param gpx the GPX object to write to the output
		 * @param output the output stream where the GPX object is written to
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final OutputStream output)
			throws IOException
		{
			final var wrapper = new NonCloseableOutputStream(output);
			try (var writer = new OutputStreamWriter(wrapper, UTF_8)) {
				write(gpx, new StreamResult(writer));
			}
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code path}.
		 *
		 * @param gpx the GPX object to write to the output
		 * @param path the output path where the GPX object is written to
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final Path path) throws IOException {
			try (var out = Files.newOutputStream(path)) {
				write(gpx, out);
			}
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code file}.
		 *
		 * @param gpx the GPX object to write to the output
		 * @param file the output file where the GPX object is written to
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final File file) throws IOException {
			write(gpx, file.toPath());
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code path}.
		 *
		 * @param gpx the GPX object to write to the output
		 * @param path the output path where the GPX object is written to
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final String path) throws IOException {
			write(gpx, Path.of(path));
		}

		/**
		 * Create an XML string representation of the given {@code gpx} object.
		 *
		 * @see GPX.Reader#fromString(String)
		 *
		 * @param gpx the GPX object to convert to a string
		 * @return the XML string representation of the given {@code gpx} object
		 * @throws NullPointerException if the given GPX object is {@code null}
		 */
		public String toString(final GPX gpx) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				write(gpx, out);
				return out.toString();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		/**
		 * Converts the given {@code gpx} object into a {@code byte[]} array.
		 * This method can be used for short term storage of GPX objects.
		 *
		 * @since 3.0
		 *
		 * @see GPX.Reader#formByteArray(byte[])
		 *
		 * @param gpx the GPX object to convert to a {@code byte[]} array
		 * @return the binary representation of the given {@code gpx} object
		 * @throws NullPointerException if the given GPX object is {@code null}
		 */
		byte[] toByteArray(final GPX gpx) {
			final var out = new ByteArrayOutputStream();
			try (var dout = new DataOutputStream(out)) {
				gpx.write(dout);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}

			return out.toByteArray();
		}

		/* *********************************************************************
		 * Factory methods.
		 * ********************************************************************/

		/**
		 * Return a new GPX writer with the given {@code indent} and number
		 * formatter, which is used for formatting {@link WayPoint#getLatitude()},
		 * {@link WayPoint#getLongitude()}, ...
		 * <p>
		 * The example below shows the <em>lat</em> and <em>lon</em> values with
		 * maximal 5 fractional digits.
		 * {@snippet lang="java":
		 * <trkpt lat="45.78068" lon="12.55368">
		 *     <ele>1.2</ele>
		 *     <time>2009-08-30T07:08:21Z</time>
		 * </trkpt>
		 * }
		 *
		 * The following table should give you a feeling about the accuracy of a
		 * given fraction digits count, at the equator.
		 *
		 * <table class="striped">
		 *     <caption><b>Maximum fraction digits accuracy</b></caption>
		 *     <thead>
		 *         <tr>
		 *             <th scope="row">Fraction digits</th>
		 *     	   	   <th scope="row">Degree</th>
		 *             <th scope="row">Distance</th>
		 *         </tr>
		 *     </thead>
		 *     <tbody>
		 *         <tr><td>0 </td><td>1           </td><td>111.31 km  </td></tr>
		 *         <tr><td>1 </td><td>0.1         </td><td> 11.13 km  </td></tr>
		 *         <tr><td>2 </td><td>0,01        </td><td>  1.1 km   </td></tr>
		 *         <tr><td>3 </td><td>0.001       </td><td>111.3 m    </td></tr>
		 *         <tr><td>4 </td><td>0.0001      </td><td> 11.1 m    </td></tr>
		 *         <tr><td>5 </td><td>0.00001     </td><td>  1.11 m   </td></tr>
		 *         <tr><td>6 </td><td>0.000001    </td><td>    0.1 m  </td></tr>
		 *         <tr><td>7 </td><td>0.0000001   </td><td> 11.1 mm   </td></tr>
		 *         <tr><td>8 </td><td>0.00000001  </td><td>  1.1 mm   </td></tr>
		 *         <tr><td>9 </td><td>0.000000001 </td><td>    0.11 mm</td></tr>
		 *     </tbody>
		 * </table>
		 *
		 * @see #of(Indent)
		 * @see #DEFAULT
		 *
		 * @since 3.0
		 *
		 * @param indent the element indentation
		 * @param maximumFractionDigits the maximum number of digits allowed in the
		 *        fraction portion of a number
		 * @return a new GPX writer
		 */
		public static Writer of(final Indent indent, final int maximumFractionDigits) {
			return new Writer(indent, maximumFractionDigits);
		}

		/**
		 * Return a new GPX writer with the given {@code indent} and with
		 * <em>maximum fraction digits</em> of
		 * {@link Writer#DEFAULT_FRACTION_DIGITS}.
		 *
		 * @see #of(Indent, int)
		 * @see #DEFAULT
		 *
		 * @since 3.0
		 *
		 * @param indent the element indentation
		 * @return a new GPX writer
		 */
		public static Writer of(final Indent indent) {
			return new Writer(indent, DEFAULT_FRACTION_DIGITS);
		}

	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @since 1.5
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param  version the GPX version
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @param extensions the XML extensions
	 * @return a new {@code GPX} object with the given data
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	public static GPX of(
		final Version version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks,
		final Document extensions
	) {
		return new GPX(
			version,
			creator,
			metadata == null || metadata.isEmpty() ? null : metadata,
			wayPoints,
			routes,
			tracks,
			XML.extensions(XML.clone(extensions))
		);
	}

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
		return of(
			Version.V11,
			creator,
			metadata,
			wayPoints,
			routes,
			tracks,
			null
		);
	}

	/**
	 * Create a new {@code GPX} object with the given data.
	 *
	 * @since 1.5
	 *
	 * @param creator the name or URL of the software that created your GPX
	 *        document. This allows others to inform the creator of a GPX
	 *        instance document that fails to validate.
	 * @param metadata the metadata about the GPS file
	 * @param wayPoints the way-points
	 * @param routes the routes
	 * @param tracks the tracks
	 * @param extensions the XML extensions
	 * @return a new {@code GPX} object with the given data
	 * @throws NullPointerException if the {@code creator}, {code wayPoints},
	 *         {@code routes} or {@code tracks} is {@code null}
	 */
	public static GPX of(
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks,
		final Document extensions
	) {
		return of(
			Version.V11,
			creator,
			metadata,
			wayPoints,
			routes,
			tracks,
			extensions
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
		final Version version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		return of(
			version,
			creator,
			metadata == null || metadata.isEmpty() ? null : metadata,
			wayPoints,
			routes,
			tracks,
			null
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	@Serial
	private Object writeReplace() {
		return new SerialProxy(SerialProxy.GPX_TYPE, this);
	}

	@Serial
	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeString(_version.getValue(), out);
		IO.writeString(_creator, out);
		IO.writeNullable(_metadata, Metadata::write, out);
		IO.writes(_wayPoints, WayPoint::write, out);
		IO.writes(_routes, Route::write, out);
		IO.writes(_tracks, Track::write, out);
		IO.writeNullable(_extensions, IO::write, out);
	}

	static GPX read(final DataInput in) throws IOException {
		return new GPX(
			Version.of(IO.readString(in)),
			IO.readString(in),
			IO.readNullable(Metadata::read, in),
			IO.reads(WayPoint::read, in),
			IO.reads(Route::read, in),
			IO.reads(Track::read, in),
			IO.readNullable(IO::readDoc, in)
		);
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	private static String name(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getName)
			.orElse(null);
	}

	private static String desc(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getDescription)
			.orElse(null);
	}

	private static String author(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getAuthor)
			.flatMap(Person::getName)
			.orElse(null);
	}

	private static String email(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getAuthor)
			.flatMap(Person::getEmail)
			.map(Email::getAddress)
			.orElse(null);
	}

	private static String url(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getAuthor)
			.flatMap(Person::getLink)
			.map(Link::getHref)
			.map(URI::toString)
			.orElse(null);
	}

	private static String urlname(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getAuthor)
			.flatMap(Person::getLink)
			.flatMap(Link::getText)
			.orElse(null);
	}

	private static String time(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getTime)
			.map(TimeFormat::format)
			.orElse(null);
	}

	private static String keywords(final GPX gpx) {
		return gpx.getMetadata()
			.flatMap(Metadata::getKeywords)
			.orElse(null);
	}


	// Define the needed writers for the different versions.
	private static XMLWriters<GPX>
	writers(final Function<? super Number, String> formatter) {
		return new XMLWriters<GPX>()
			.v00(XMLWriter.attr("version").map(gpx -> gpx._version._value))
			.v00(XMLWriter.attr("creator").map(GPX::getCreator))
			.v11(XMLWriter.ns(Version.V11.getNamespaceURI()))
			.v10(XMLWriter.ns(Version.V10.getNamespaceURI()))
			.v11(Metadata.writer(formatter).flatMap(GPX::getMetadata))
			.v10(XMLWriter.elem("name").map(GPX::name))
			.v10(XMLWriter.elem("desc").map(GPX::desc))
			.v10(XMLWriter.elem("author").map(GPX::author))
			.v10(XMLWriter.elem("email").map(GPX::email))
			.v10(XMLWriter.elem("url").map(GPX::url))
			.v10(XMLWriter.elem("urlname").map(GPX::urlname))
			.v10(XMLWriter.elem("time").map(GPX::time))
			.v10(XMLWriter.elem("keywords").map(GPX::keywords))
			.v10(XMLWriter.elems(WayPoint.xmlWriter(Version.V10,"wpt", formatter)).map(GPX::getWayPoints))
			.v11(XMLWriter.elems(WayPoint.xmlWriter(Version.V11,"wpt", formatter)).map(GPX::getWayPoints))
			.v10(XMLWriter.elems(Route.xmlWriter(Version.V10, formatter)).map(GPX::getRoutes))
			.v11(XMLWriter.elems(Route.xmlWriter(Version.V11, formatter)).map(GPX::getRoutes))
			.v10(XMLWriter.elems(Track.xmlWriter(Version.V10, formatter)).map(GPX::getTracks))
			.v11(XMLWriter.elems(Track.xmlWriter(Version.V11, formatter)).map(GPX::getTracks))
			.v00(XMLWriter.doc("extensions").flatMap(GPX::getExtensions));
	}


	// Define the needed readers for the different versions.
	private static XMLReaders
	readers(final Function<? super String, Length> lengthParser) {
		return new XMLReaders()
			.v00(XMLReader.attr("version").map(Version::of, Version.V11))
			.v00(XMLReader.attr("creator"))
			.v11(Metadata.READER)
			.v10(XMLReader.elem("name"))
			.v10(XMLReader.elem("desc"))
			.v10(XMLReader.elem("author"))
			.v10(XMLReader.elem("email"))
			.v10(XMLReader.elem("url"))
			.v10(XMLReader.elem("urlname"))
			.v10(XMLReader.elem("time").map(TimeFormat::parse))
			.v10(XMLReader.elem("keywords"))
			.v10(Bounds.READER)
			.v10(XMLReader.elems(WayPoint.xmlReader(Version.V10, "wpt", lengthParser)))
			.v11(XMLReader.elems(WayPoint.xmlReader(Version.V11, "wpt", lengthParser)))
			.v10(XMLReader.elems(Route.xmlReader(Version.V10, lengthParser)))
			.v11(XMLReader.elems(Route.xmlReader(Version.V11, lengthParser)))
			.v10(XMLReader.elems(Track.xmlReader(Version.V10, lengthParser)))
			.v11(XMLReader.elems(Track.xmlReader(Version.V11, lengthParser)))
			.v00(XMLReader.doc("extensions"));
	}


	static XMLWriter<GPX> xmlWriter(
		final Version version,
		final Function<? super Number, String> formatter
	) {
		return XMLWriter.elem("gpx", writers(formatter).writers(version));
	}

	static XMLReader<GPX> xmlReader(
		final Version version,
		final Function<? super String, Length> lengthParser
	) {
		return XMLReader.elem(
			version == Version.V10 ? GPX::toGPXv10 : GPX::toGPXv11,
			"gpx",
			readers(lengthParser).readers(version)
		);
	}

	@SuppressWarnings("unchecked")
	private static GPX toGPXv11(final Object[] v) {
		return new GPX(
			(Version)v[0],
			(String)v[1],
			(Metadata)v[2],
			(List<WayPoint>)v[3],
			(List<Route>)v[4],
			(List<Track>)v[5],
			XML.extensions((Document)v[6])
		);
	}

	@SuppressWarnings("unchecked")
	private static GPX toGPXv10(final Object[] v) {
		return new GPX(
			(Version)v[0],
			(String)v[1],
			Metadata.of(
				(String)v[2],
				(String)v[3],
				Person.of(
					(String)v[4],
					v[5] != null
						? Email.of((String)v[5])
						: null,
					v[6] != null
						? Link.of((String)v[6], (String)v[7], null)
						: null
				),
				null,
				null,
				(Instant)v[8],
				(String)v[9],
				(Bounds)v[10]
			),
			(List<WayPoint>)v[11],
			(List<Route>)v[12],
			(List<Track>)v[13],
			XML.extensions((Document)v[14])
		);
	}


	/* *************************************************************************
	 *  Write and read GPX files
	 * ************************************************************************/

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code path}.
	 * This method is a shortcut for
	 * {@snippet lang="java":
	 * GPX.Writer.DEFAULT.write(gpx, path);
	 * }
	 *
	 * @see Writer
	 *
	 * @since 1.1
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final Path path) throws IOException {
		Writer.DEFAULT.write(gpx, path);
	}

	/**
	 * Read a GPX object from the given {@code input} stream.
	 * This method is a shortcut for
	 * {@snippet lang="java":
	 * GPX.Reader.DEFAULT.read(path);
	 * }
	 *
	 * @see Reader
	 *
	 * @param path the input path from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final Path path) throws IOException {
		return Reader.DEFAULT.read(path);
	}

}
