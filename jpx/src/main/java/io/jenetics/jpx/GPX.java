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
import static java.util.Objects.requireNonNull;
import static io.jenetics.jpx.Lists.copy;
import static io.jenetics.jpx.Lists.immutable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
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
 * <gpx xmlns="http://www.topografix.com/GPX/1/1" version="1.1" creator="JPX" >
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
 * @version 1.5
 * @since 1.0
 */
public final class GPX implements Serializable {

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
			switch (version) {
				case "1.0": return V10;
				case "1.1": return V11;
				default: throw new IllegalArgumentException(format(
					"Unknown version string: '%s'.", version
				));
			}
		}
	}

	/**
	 * The default version number: 1.1.
	 *
	 * @deprecated Use {@link GPX.Version} instead
	 */
	@Deprecated
	public static final String VERSION = Version.V11._value;

	private static final String _CREATOR = "JPX - https://github.com/jenetics/jpx";

	/**
	 * The default creator string.
	 *
	 * @deprecated Will be removed without replacement
	 */
	@Deprecated
	public static final String CREATOR = _CREATOR;

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
		_wayPoints = immutable(wayPoints);
		_routes = immutable(routes);
		_tracks = immutable(tracks);
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
		return obj == this ||
			obj instanceof GPX &&
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
			copy(wayPoints, _wayPoints);
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
			copy(routes, _routes);
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
			copy(tracks, _tracks);
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
			return new Filter<WayPoint, Builder>() {

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
							.collect(Collectors.toList())
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
							.collect(Collectors.toList())
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
			return new Filter<Route, Builder>() {
				@Override
				public Filter<Route, Builder> filter(
					final Predicate<? super Route> predicate
				) {
					routes(
						_routes.stream()
							.filter(predicate)
							.collect(Collectors.toList())
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
							.collect(Collectors.toList())
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
							.collect(Collectors.toList())
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
			return new Filter<Track, Builder>() {
				@Override
				public Filter<Track, Builder> filter(
					final Predicate<? super Track> predicate
				) {
					tracks(
						_tracks.stream()
							.filter(predicate)
							.collect(Collectors.toList())
					);

					return this;
				}

				@Override
				public Filter<Track, Builder> map(
					final Function<? super Track, ? extends Track> mapper
				) {
					tracks(
						_tracks.stream()
							.map(mapper)
							.collect(Collectors.toList())
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
							.collect(Collectors.toList())
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
	 * Create a new GPX builder with the given GPX version and creator string.
	 *
	 * @param version the GPX version string
	 * @param creator the GPX creator
	 * @return new GPX builder
	 * @throws NullPointerException if one of the arguments is {@code null}
	 *
	 * @deprecated Use {@link #builder(Version, String)} instead.
	 */
	@Deprecated
	public static Builder builder(final String version, final String creator) {
		return new Builder(Version.of(version), creator);
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
			final XMLInputFactory factory = XMLInputFactory.newInstance();
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
				throw new InvalidObjectException("Invalid 'gpx' input.");
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
	 * @see GPX#writer()
	 * @see GPX#writer(String)
	 *
	 * @version 1.3
	 * @since 1.3
	 */
	public static final class Writer {

		private final String _indent;

		private Writer(final String indent) {
			_indent = indent;
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
			final XMLOutputFactory factory = XMLOutputFactory.newInstance();
			try (XMLStreamWriterAdapter xml = writer(factory, output)) {
				xml.writeStartDocument("UTF-8", "1.0");
				GPX.xmlWriter(gpx._version).write(xml, gpx);
				xml.writeEndDocument();
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
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
		 * @param file the output file where the GPX object is written to
		 * @throws IOException if the writing of the GPX object fails
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final File file) throws IOException {
			try (FileOutputStream out = new FileOutputStream(file);
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
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final Path path) throws IOException {
			write(gpx, path.toFile());
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
			write(gpx, Paths.get(path));
		}

		/**
		 * Create a XML string representation of the given {@code gpx} object.
		 *
		 * @param gpx the GPX object to convert to a string
		 * @return the XML string representation of the given {@code gpx} object
		 * @throws NullPointerException if the given given GPX object is
		 *         {@code null}
		 */
		public String toString(final GPX gpx) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				write(gpx, out);
				return new String(out.toByteArray());
			} catch (IOException e) {
				throw new IllegalStateException("Unexpected error.", e);
			}
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
	 * @throws IllegalArgumentException if the given GPX {@code version} string
	 *         is neither "1.0" nor "1.1"
	 *
	 * @deprecated Use {@link #of(Version, String, Metadata, List, List, List)}
	 *             instead
	 */
	@Deprecated
	public static GPX of(
		final String version,
		final String creator,
		final Metadata metadata,
		final List<WayPoint> wayPoints,
		final List<Route> routes,
		final List<Track> tracks
	) {
		return of(
			Version.of(version),
			creator,
			metadata,
			wayPoints,
			routes,
			tracks,
			null
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new Serial(Serial.GPX_TYPE, this);
	}

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
	private static final XMLWriters<GPX> WRITERS = new XMLWriters<GPX>()
		.v00(XMLWriter.attr("version").map(gpx -> gpx._version._value))
		.v00(XMLWriter.attr("creator").map(gpx -> gpx._creator))
		.v11(XMLWriter.ns("http://www.topografix.com/GPX/1/1"))
		.v10(XMLWriter.ns("http://www.topografix.com/GPX/1/0"))
		.v11(Metadata.WRITER.map(gpx -> gpx._metadata))
		.v10(XMLWriter.elem("name").map(GPX::name))
		.v10(XMLWriter.elem("desc").map(GPX::desc))
		.v10(XMLWriter.elem("author").map(GPX::author))
		.v10(XMLWriter.elem("email").map(GPX::email))
		.v10(XMLWriter.elem("url").map(GPX::url))
		.v10(XMLWriter.elem("urlname").map(GPX::urlname))
		.v10(XMLWriter.elem("time").map(GPX::time))
		.v10(XMLWriter.elem("keywords").map(GPX::keywords))
		.v10(XMLWriter.elems(WayPoint.xmlWriter(Version.V10,"wpt")).map(gpx -> gpx._wayPoints))
		.v11(XMLWriter.elems(WayPoint.xmlWriter(Version.V11,"wpt")).map(gpx -> gpx._wayPoints))
		.v10(XMLWriter.elems(Route.xmlWriter(Version.V10)).map(gpx -> gpx._routes))
		.v11(XMLWriter.elems(Route.xmlWriter(Version.V11)).map(gpx -> gpx._routes))
		.v10(XMLWriter.elems(Track.xmlWriter(Version.V10)).map(gpx -> gpx._tracks))
		.v11(XMLWriter.elems(Track.xmlWriter(Version.V11)).map(gpx -> gpx._tracks))
		.v00(XMLWriter.doc("extensions").map(gpx -> gpx._extensions));


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


	static XMLWriter<GPX> xmlWriter(final Version version) {
		return XMLWriter.elem("gpx", WRITERS.writers(version));
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
	 * Return a new GPX writer with the given {@code indent}.
	 *
	 * @since 1.3
	 *
	 * @see #writer()
	 *
	 * @param indent the element indentation
	 * @return a new GPX writer
	 */
	public static Writer writer(final String indent) {
		return new Writer(indent);
	}

	/**
	 * Return a new GPX writer with no indentation.
	 *
	 * @since 1.3
	 *
	 * @see #writer(String)
	 *
	 * @return a new GPX writer
	 */
	public static Writer writer() {
		return new Writer(null);
	}

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
		writer().write(gpx, output);
	}

	/**
	 * Writes the given {@code gpx} object (in GPX XML format) to the given
	 * {@code output} stream.
	 *
	 * @since 1.1
	 *
	 * @param gpx the GPX object to write to the output
	 * @param path the output path where the GPX object is written to
	 * @throws IOException if the writing of the GPX object fails
	 * @throws NullPointerException if one of the given arguments is {@code null}
	 */
	public static void write(final GPX gpx, final Path path) throws IOException {
		writer().write(gpx, path);
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
		writer().write(gpx, path);
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
	 *
	 * @deprecated Use {@code GPX.writer(indent).write(gpx, output)} instead
	 */
	@Deprecated
	public static void write(
		final GPX gpx,
		final OutputStream output,
		final String indent
	)
		throws IOException
	{
		writer(indent).write(gpx, output);
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
	 *
	 * @deprecated Use {@code GPX.writer(indent).write(gpx, path)} instead
	 */
	@Deprecated
	public static void write(final GPX gpx, final Path path, final String indent)
		throws IOException
	{
		writer(indent).write(gpx, path);
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
	 *
	 * @deprecated Use {@code GPX.writer(indent).write(gpx, path);} instead
	 */
	@Deprecated
	public static void write(final GPX gpx, final String path, final String indent)
		throws IOException
	{
		writer(indent).write(gpx, path);
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
	 * Read an GPX object from the given {@code input} stream.
	 *
	 * @since 1.1
	 *
	 * @param input the input stream from where the GPX date is read
	 * @param lenient if {@code true}, out-of-range and syntactical errors are
	 *        ignored. E.g. a {@code WayPoint} with {@code lat} values not in
	 *        the valid range of [-90..90] are ignored/skipped.
	 * @return the GPX object read from the input stream
	 * @throws IOException if the GPX object can't be read
	 * @throws NullPointerException if the given {@code input} stream is
	 *         {@code null}
	 *
	 * @see #reader(GPX.Version, GPX.Reader.Mode)
	 * @deprecated Use {@code GPX.reader(Mode.LENIENT).read(input)} instead
	 */
	@Deprecated
	public static GPX read(final InputStream input, final boolean lenient)
		throws IOException
	{
		return reader(Version.V11, lenient ? Mode.LENIENT : Mode.STRICT)
			.read(input);
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
	 *
	 * @see #reader(GPX.Version, GPX.Reader.Mode)
	 * @deprecated Use {@code GPX.reader(Mode.LENIENT).read(path)} instead
	 */
	@Deprecated
	public static GPX read(final Path path, final boolean lenient)
		throws IOException
	{
		return reader(Version.V11, lenient ? Mode.LENIENT : Mode.STRICT)
			.read(path);
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
	 *
	 * @see #reader(GPX.Version, GPX.Reader.Mode)
	 * @deprecated Use {@code GPX.reader(lenient).read(path)} instead
	 */
	@Deprecated
	public static GPX read(final String path, final boolean lenient)
		throws IOException
	{
		return reader(Version.V11, lenient ? Mode.LENIENT : Mode.STRICT)
			.read(path);
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
