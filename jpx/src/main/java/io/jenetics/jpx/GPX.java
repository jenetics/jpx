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
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Lists.copyOf;
import static io.jenetics.jpx.Lists.copyTo;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.w3c.dom.Document;

import io.jenetics.jpx.GPX.Reader.Mode;

/**
 * GPX documents contain a metadata header, followed by way-points, routes, and
 * tracks. You can add your own elements to the extensions section of the GPX
 * document.
 * <p>
 * <em><b>Examples:</b></em>
 * <p>
 * <b>Creating a GPX object with one track-segment and 3 track-points</b>
 * <pre>{@code
 * final GPX gpx = GPX.builder()
 *     .addTrack(track -> track
 *         .addSegment(segment -> segment
 *             .addPoint(p -> p.lat(48.20100).lon(16.31651).ele(283))
 *             .addPoint(p -> p.lat(48.20112).lon(16.31639).ele(278))
 *             .addPoint(p -> p.lat(48.20126).lon(16.31601).ele(274))))
 *     .build();
 * }</pre>
 *
 * <b>Writing a GPX file</b>
 * <pre>{@code
 * final var indent = "    ";
 * GPX.writer(indent).write(gpx, Path.of("points.gpx"));
 * }</pre>
 *
 * This will produce the following output.
 * <pre>{@code
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
 * }</pre>
 *
 * <b>Reading a GPX file</b>
 * <pre>{@code
 * final GPX gpx = GPX.read("points.xml");
 * }</pre>
 *
 * <b>Reading erroneous GPX files</b>
 * <pre>{@code
 * final boolean lenient = true;
 * final GPX gpx = GPX.read("track.xml", lenient);
 * }</pre>
 *
 * This allows to read otherwise invalid GPX files, like
 * <pre>{@code
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
 * }</pre>
 *
 * which is read as (if you write it again)
 * <pre>{@code
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
 * }</pre>
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
	 * <pre>{@code
	 * <extensions>
	 *     ...
	 * </extensions>
	 * }</pre>
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
		 * <pre>{@code
		 * <extensions>
		 *     ...
		 * </extensions>
		 * }</pre>
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
		 * <pre>{@code
		 * final GPX filtered = gpx.toBuilder()
		 *     .wayPointFilter()
		 *         .filter(wp -> wp.getTime().isPresent())
		 *         .build())
		 *     .build();
		 * }</pre>
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
					wayPoints(
						_wayPoints.stream()
							.filter(predicate)
							.collect(Collectors.toList())
					);

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
		 * <pre>{@code
		 * final GPX filtered = gpx.toBuilder()
		 *     .routeFilter()
		 *         .filter(Route::nonEmpty)
		 *         .build())
		 *     .build();
		 * }</pre>
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
		 * <pre>{@code
		 * final GPX merged = gpx.toBuilder()
		 *     .trackFilter()
		 *         .map(track -> track.toBuilder()
		 *             .listMap(Filters::mergeSegments)
		 *             .filter(TrackSegment::nonEmpty)
		 *             .build())
		 *         .build()
		 *     .build();
		 * }</pre>
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
	 * @see GPX#reader()
	 * @see GPX#reader(Version, Reader.Mode)
	 *
	 * @version 1.3
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

		private final XMLReader<GPX> _reader;
		private final Mode _mode;

		private Reader(final XMLReader<GPX> reader, final Mode mode) {
			_reader = requireNonNull(reader);
			_mode = requireNonNull(mode);
		}

		/**
		 * Return the current reader mode.
		 *
		 * @return the current reader mode
		 */
		public Mode getMode() {
			return _mode;
		}

		/**
		 * Read a GPX object from the given {@code input} stream.
		 *
		 * @param input the input stream from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 * @throws InvalidObjectException if the gpx input is invalid.
		 */
		public GPX read(final InputStream input)
			throws IOException, InvalidObjectException
		{
			final XMLInputFactory factory = XMLProvider.provider().xmlInputFactory();
			try  (XMLStreamReaderAdapter reader = new XMLStreamReaderAdapter(
						factory.createXMLStreamReader(input)))
			{
				if (reader.hasNext()) {
					reader.next();
					return _reader.read(reader, _mode == Mode.LENIENT);
				} else {
					throw new InvalidObjectException("No 'gpx' element found.");
				}
			} catch (XMLStreamException e) {
				throw new InvalidObjectException("Invalid 'gpx' input: " + e.getMessage());
			} catch (IllegalArgumentException e) {
				throw (InvalidObjectException)new InvalidObjectException(e.getMessage())
						.initCause(e);
			}
		}

		/**
		 * Read a GPX object from the given {@code input} stream.
		 *
		 * @param file the input file from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 */
		public GPX read(final File file) throws IOException {
			try (FileInputStream fin = new FileInputStream(file);
				 BufferedInputStream bin = new BufferedInputStream(fin))
			{
				return read(bin);
			}
		}

		/**
		 * Read a GPX object from the given {@code input} stream.
		 *
		 * @param path the input path from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 */
		public GPX read(final Path path) throws IOException {
			return read(path.toFile());
		}

		/**
		 * Read a GPX object from the given {@code input} stream.
		 *
		 * @param path the input path from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 */
		public GPX read(final String path) throws IOException {
			return read(Paths.get(path));
		}

		/**
		 * Create a GPX object from the given GPX-XML string.
		 *
		 * @param xml the GPX XML string
		 * @return the GPX object created from the given XML string
		 * @throws IllegalArgumentException if the given {@code xml} is not a
		 *         valid GPX XML string
		 * @throws NullPointerException if the given {@code xml} string is
		 *         {@code null}
		 */
		public GPX fromString(final String xml) {
			final byte[] bytes = xml.getBytes();
			final ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			try {
				return read(in);
			} catch (InvalidObjectException e) {
				if (e.getCause() instanceof IllegalArgumentException) {
					throw (IllegalArgumentException)e.getCause();
				}
				throw new IllegalArgumentException(e);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
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
		 * The default value for the <em>maximum fraction digits</em>.
		 */
		public static final int DEFAULT_MAXIMUM_FRACTION_DIGITS = 6;

		private final String _indent;
		private final int _maximumFractionDigits;

		private Writer(final String indent, final int maximumFractionDigits) {
			_indent = indent;
			_maximumFractionDigits = maximumFractionDigits;
		}

		/**
		 * Return the indentation string this GPX writer is using. If the
		 * indentation string is {@link Optional#empty()}, the GPX file consists
		 * of one line.
		 *
		 * @return the indentation string
		 */
		public Optional<String> getIndent() {
			return Optional.ofNullable(_indent);
		}

		/**
		 * Return the maximum number of digits allowed in the fraction portion
		 * of the written numbers like <em>latitude</em> and <em>longitude</em>.
		 *
		 * @return the maximum number of digits allowed in the fraction portion
		 * 		   of the written numbers
		 */
		public int getMaximumFractionDigits() {
			return _maximumFractionDigits;
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code output} stream.
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
			final XMLOutputFactory factory = XMLProvider.provider().xmlOutputFactory();
			try (XMLStreamWriterAdapter xml = writer(factory, output)) {
				xml.writeStartDocument("UTF-8", "1.0");
				final var writer = GPX.xmlWriter(
					gpx._version,
					formatter(_maximumFractionDigits)
				);
				writer.write(xml, gpx);
				xml.writeEndDocument();
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		private static Function<? super Number, String>
		formatter(final int maximumFractionDigits) {
			final var format = NumberFormat.getNumberInstance(Locale.ENGLISH);
			format.setMaximumFractionDigits(maximumFractionDigits);

			return value -> value != null ? format.format(value) : null;
		}

		private XMLStreamWriterAdapter writer(
			final XMLOutputFactory factory,
			final OutputStream output
		)
			throws XMLStreamException
		{
			final NonCloseableOutputStream out =
				new NonCloseableOutputStream(output);

			return _indent == null
				? new XMLStreamWriterAdapter(factory
					.createXMLStreamWriter(out, "UTF-8"))
				: new IndentingXMLStreamWriter(factory
					.createXMLStreamWriter(out, "UTF-8"), _indent);
		}

		/**
		 * Writes the given {@code gpx} object (in GPX XML format) to the given
		 * {@code output} stream.
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
		 * {@code output} stream.
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
		 * {@code output} stream.
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
				throw new AssertionError("Unexpected error: " + e);
			}
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
		 * <pre>{@code
		 * <trkpt lat="45.78068" lon="12.55368">
		 *     <ele>1.2</ele>
		 *     <time>2009-08-30T07:08:21Z</time>
		 * </trkpt>
		 * }</pre>
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
		 * @see #of(String)
		 * @see #of()
		 *
		 * @since 3.0
		 *
		 * @param indent the element indentation
		 * @param maximumFractionDigits the maximum number of digits allowed in the
		 *        fraction portion of a number
		 * @return a new GPX writer
		 */
		public static Writer of(final String indent, final int maximumFractionDigits) {
			return new Writer(indent, maximumFractionDigits);
		}

		/**
		 * Return a new GPX writer with the given {@code indent} and with
		 * <em>maximum fraction digits</em> of
		 * {@link Writer#DEFAULT_MAXIMUM_FRACTION_DIGITS}.
		 *
		 * @see #of(String, int)
		 * @see #of()
		 *
		 * @since 3.0
		 *
		 * @param indent the element indentation
		 * @return a new GPX writer
		 */
		public static Writer of(final String indent) {
			return new Writer(indent, DEFAULT_MAXIMUM_FRACTION_DIGITS);
		}

		/**
		 * Return the default GPX writer, with no indention and fraction digits
		 * of {@link #DEFAULT_MAXIMUM_FRACTION_DIGITS}.
		 *
		 * @see #of(String, int)
		 * @see #of(String)
		 *
		 * @since 3.0
		 *
		 * @return the default GPX writer
		 */
		public static Writer of() {
			return new Writer(null, DEFAULT_MAXIMUM_FRACTION_DIGITS);
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
			.map(ZonedDateTimeFormat::format)
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
			.v00(XMLWriter.attr("creator").map(gpx -> gpx._creator))
			.v11(XMLWriter.ns(Version.V11.getNamespaceURI()))
			.v10(XMLWriter.ns(Version.V10.getNamespaceURI()))
			.v11(Metadata.writer(formatter).map(gpx -> gpx._metadata))
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
			.v00(XMLWriter.doc("extensions").map(gpx -> gpx._extensions));
	}


	// Define the needed readers for the different versions.
	private static final XMLReaders READERS = new XMLReaders()
		.v00(XMLReader.attr("version").map(Version::of))
		.v00(XMLReader.attr("creator"))
		.v11(Metadata.READER)
		.v10(XMLReader.elem("name"))
		.v10(XMLReader.elem("desc"))
		.v10(XMLReader.elem("author"))
		.v10(XMLReader.elem("email"))
		.v10(XMLReader.elem("url"))
		.v10(XMLReader.elem("urlname"))
		.v10(XMLReader.elem("time").map(ZonedDateTimeFormat::parse))
		.v10(XMLReader.elem("keywords"))
		.v10(Bounds.READER)
		.v10(XMLReader.elems(WayPoint.xmlReader(Version.V10, "wpt")))
		.v11(XMLReader.elems(WayPoint.xmlReader(Version.V11, "wpt")))
		.v10(XMLReader.elems(Route.xmlReader(Version.V10)))
		.v11(XMLReader.elems(Route.xmlReader(Version.V11)))
		.v10(XMLReader.elems(Track.xmlReader(Version.V10)))
		.v11(XMLReader.elems(Track.xmlReader(Version.V11)))
		.v00(XMLReader.doc("extensions"));


	static XMLWriter<GPX> xmlWriter(
		final Version version,
		final Function<? super Number, String> formatter
	) {
		return XMLWriter.elem("gpx", writers(formatter).writers(version));
	}

	static XMLReader<GPX> xmlReader(final Version version) {
		return XMLReader.elem(
			version == Version.V10 ? GPX::toGPXv10 : GPX::toGPXv11,
			"gpx",
			READERS.readers(version)
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
				(ZonedDateTime)v[8],
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
	 * {@code output} stream.
	 *
	 * @see Writer#of(String, int)
	 * @see Writer#of(String)
	 * @see Writer#of()
	 *
	 * @param gpx the GPX object to write to the output
	 * @param output the output stream where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final OutputStream output)
		throws IOException
	{
		Writer.of().write(gpx, output);
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @see Writer#of(String, int)
	 * @see Writer#of(String)
	 * @see Writer#of()
	 *
	 * @since 1.1
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final Path path) throws IOException {
		Writer.of().write(gpx, path);
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @see Writer#of(String, int)
	 * @see Writer#of(String)
	 * @see Writer#of()
	 *
	 * @since 3.0
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final String path) throws IOException {
		Writer.of().write(gpx, path);
	}


	/**
	 * Return a GPX reader, reading GPX files with the given version and in the
	 * given reading mode.
	 *
	 * @since 1.3
	 *
	 * @see #reader()
	 *
	 * @param version the GPX version to read
	 * @param mode the reading mode
	 * @return a new GPX reader object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Reader reader(final Version version, final Mode mode) {
		return new Reader(GPX.xmlReader(version), mode);
	}

	/**
	 * Return a GPX reader, reading GPX files with the given version and in
	 * strict reading mode.
	 *
	 * @since 1.3
	 *
	 * @see #reader()
	 *
	 * @param version the GPX version to read
	 * @return a new GPX reader object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Reader reader(final Version version) {
		return new Reader(GPX.xmlReader(version), Mode.STRICT);
	}

	/**
	 * Return a GPX reader, reading GPX files with version 1.1 and in the given
	 * reading mode.
	 *
	 * @since 1.3
	 *
	 * @see #reader()
	 *
	 * @param mode the reading mode
	 * @return a new GPX reader object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Reader reader(final Mode mode) {
		return new Reader(GPX.xmlReader(Version.V11), mode);
	}

	/**
	 * Return a GPX reader, reading GPX files (v1.1) with reading mode
	 * {@link Mode#STRICT}.
	 *
	 * @since 1.3
	 *
	 * @see #reader(Version, Reader.Mode)
	 *
	 * @return a new GPX reader object
	 */
	public static Reader reader() {
		return reader(Version.V11, Mode.STRICT);
	}


	/**
	 * Read an GPX object from the given {@code input} stream. This method is a
	 * shortcut for
	 * <pre>{@code
	 * final GPX gpx = GPX.reader().read(input);
	 * }</pre>
	 *
	 * @param input the input stream from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 * @throws InvalidObjectException if the gpx input is invalid.
	 */
	public static GPX read(final InputStream input) throws IOException {
		return reader(Version.V11, Mode.STRICT).read(input);
	}


	/**
	 * Read an GPX object from the given {@code input} stream. This method is a
	 * shortcut for
	 * <pre>{@code
	 * final GPX gpx = GPX.reader().read(path);
	 * }</pre>
	 *
	 * @param path the input path from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final Path path) throws IOException {
		return reader(Version.V11, Mode.STRICT).read(path);
	}


	/**
	 * Read an GPX object from the given {@code input} stream. This method is a
	 * shortcut for
	 * <pre>{@code
	 * final GPX gpx = GPX.reader().read(path);
	 * }</pre>
	 *
	 * @param path the input path from where the GPX date is read
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 */
	public static GPX read(final String path) throws IOException {
		return reader(Version.V11, Mode.STRICT).read(path);
	}

}
