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
import static io.jenetics.jpx.XMLWriter.elem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
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
import javax.xml.stream.XMLStreamWriter;

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
 * @version !__version__!
 * @since 1.0
 */
public final class GPX implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * Represents the available GPX versions.
	 *
	 * @version !__version__!
	 * @since !__version__!
	 */
	public enum Version {

		/**
		 * The GPX version 1.0. This version can only be read.
		 */
		v10("1.0"),

		/**
		 * The GPX version 1.1. This is the default version and can be read and
		 * written.
		 */
		v11("1.1");

		private final String value;

		Version(final String value) {
			this.value = value;
		}
	}

	/**
	 * The default version number: 1.1.
	 */
	public static final String VERSION = Version.v11.value;

	/**
	 * The default creator string.
	 */
	public static final String CREATOR = "JPX - https://github.com/jenetics/jpx";

	private final String _creator;
	private final String _version;
	private final Metadata _metadata;
	private final List<WayPoint> _wayPoints;
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
		private String _version;
		private Metadata _metadata;
		private final List<WayPoint> _wayPoints = new ArrayList<>();
		private final List<Route> _routes = new ArrayList<>();
		private final List<Track> _tracks = new ArrayList<>();

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
		 * Return the current version value.
		 *
		 * @since 1.1
		 *
		 * @return the current version value
		 */
		public String version() {
			return _version;
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


	/**
	 * Class for reading GPX files. A reader instance can be created by the
	 * {@code GPX.reader} factory methods.
	 *
	 * @see GPX#reader()
	 * @see GPX#reader(Reader.Mode)
	 * @see GPX#reader(Version)
	 * @see GPX#reader(Version, Reader.Mode)
	 *
	 * @version !__version__!
	 * @since !__version__!
	 */
	public static final class Reader {

		/**
		 * The possible GPX reader modes.
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
		 * Read an GPX object from the given {@code input} stream.
		 *
		 * @param input the input stream from where the GPX date is read
		 * @return the GPX object read from the input stream
		 * @throws IOException if the GPX object can't be read
		 * @throws NullPointerException if the given {@code input} stream is
		 *         {@code null}
		 */
		public GPX read(final InputStream input)
			throws IOException
		{
			final XMLInputFactory factory = XMLInputFactory.newFactory();
			try {
				try (CloseableXMLStreamReader reader =
					 new CloseableXMLStreamReader(factory.createXMLStreamReader(input)))
				{
					if (reader.hasNext()) {
						reader.next();
						return _reader.read(reader, _mode == Mode.LENIENT);
					} else {
						throw new IOException("No 'gpx' element found.");
					}
				}
			} catch (XMLStreamException e) {
				throw new IOException(e);
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
		public GPX read(final Path path) throws IOException {
			try (FileInputStream in = new FileInputStream(path.toFile());
				 BufferedInputStream bin = new BufferedInputStream(in))
			{
				return read(bin);
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
		public GPX read(final String path) throws IOException {
			return read(Paths.get(path));
		}

	}

	/**
	 * Class for writing GPX files. A writer instance can be created by the
	 * {@code GPX.writer} factory methods.
	 *
	 * @see GPX#writer()
	 * @see GPX#writer(String)
	 *
	 * @version !__version__!
	 * @since !__version__!
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
			final XMLOutputFactory factory = XMLOutputFactory.newFactory();
			try (CloseableXMLStreamWriter writer = writer(factory, output)) {
				writer.writeStartDocument("UTF-8", "1.0");
				gpx.write(writer);
				writer.writeEndDocument();
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}

		private CloseableXMLStreamWriter writer(
			final XMLOutputFactory factory,
			final OutputStream output
		)
			throws XMLStreamException
		{
			final NonCloseableOutputStream out =
				new NonCloseableOutputStream(output);

			return _indent == null
				? new CloseableXMLStreamWriter(factory
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
		 * @throws NullPointerException if one of the given arguments is
		 *         {@code null}
		 */
		public void write(final GPX gpx, final String path) throws IOException {
			write(gpx, Paths.get(path));
		}

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
		IO.writeString(_version, out);
		IO.writeString(_creator, out);
		IO.writeNullable(_metadata, Metadata::write, out);
		IO.writes(_wayPoints, WayPoint::write, out);
		IO.writes(_routes, Route::write, out);
		IO.writes(_tracks, Track::write, out);
	}

	static GPX read(final DataInput in) throws IOException {
		return new GPX(
			IO.readString(in),
			IO.readString(in),
			IO.readNullable(Metadata::read, in),
			IO.reads(WayPoint::read, in),
			IO.reads(Route::read, in),
			IO.reads(Track::read, in)
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
		WRITER.write(writer, this);
	}

	static final XMLWriter<GPX> WRITER = elem("gpx",
		XMLWriter.attr("version").map(gpx -> gpx._version),
		XMLWriter.attr("creator").map(gpx -> gpx._creator),
		XMLWriter.ns("http://www.topografix.com/GPX/1/1"),
		Metadata.WRITER.map(gpx -> gpx._metadata),
		XMLWriter.elems(WayPoint.writer("wpt")).map(gpx -> gpx._wayPoints),
		XMLWriter.elems(Route.WRITER).map(gpx -> gpx._routes),
		XMLWriter.elems(Track.WRITER).map(gpx -> gpx._tracks)
	);

	@SuppressWarnings("unchecked")
	static final XMLReader<GPX> READER = XMLReader.elem(
		a -> GPX.of(
			(String)a[0],
			(String)a[1],
			(Metadata)a[2],
			(List<WayPoint>)a[3],
			(List<Route>)a[4],
			(List<Track>)a[5]
		),
		"gpx",
		XMLReader.attr("version"),
		XMLReader.attr("creator"),
		Metadata.READER,
		XMLReader.elems(WayPoint.reader("wpt")),
		XMLReader.elems(Route.READER),
		XMLReader.elems(Track.READER)
	);

	@SuppressWarnings("unchecked")
	private static final XMLReader<GPX> READER_v10 = XMLReader.elem(
		a -> GPX.of(
			(String)a[0],
			(String)a[1],
			metadata(a),
			(List<WayPoint>)a[11],
			(List<Route>)a[12],
			(List<Track>)a[13]
		),
		"gpx",
		XMLReader.attr("version"),
		XMLReader.attr("creator"),

		// Metadata
		XMLReader.elem("name"),
		XMLReader.elem("desc"),
		XMLReader.elem("author"),
		XMLReader.elem("email"),
		XMLReader.elem("url"),
		XMLReader.elem("urlname"),
		XMLReader.elem("time").map(ZonedDateTimeFormat::parse),
		XMLReader.elem("keywords"),
		Bounds.READER,

		XMLReader.elems(WayPoint.reader("wpt")),
		XMLReader.elems(Route.READER),
		XMLReader.elems(Track.READER)
	);

	private static Metadata metadata(final Object[] v) {
		final Person author = Person.of(
			(String)v[4],
			v[5] != null ? Email.of((String)v[5]) : null,
			v[6] != null ? Link.of((String)v[6], (String)v[7], null) : null
		);

		return Metadata.of(
			(String)v[2],
			(String)v[3],
			author.isEmpty() ? null : author,
			null,
			null,
			(ZonedDateTime)v[8],
			(String)v[9],
			(Bounds)v[10]
		);
	}


	/* *************************************************************************
	 *  Write and read GPX files
	 * ************************************************************************/

	/**
	 * Return a new GPX writer with the given {@code indent}.
	 *
	 * @since !__version__!
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
	 * @since !__version__!
	 *
	 * @see #writer(String)
	 *
	 * @return a new GPX writer
	 */
	public static Writer writer() {
		return writer(null);
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
	 * @deprecated Use {@code GPX.writer(indent).write(gpx, output);} instead
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
	 * @deprecated Use {@code GPX.writer(indent).write(gpx, path);} instead
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
	 * Return a GPX reader, reading GPX files with the given version and in
	 * reading mode.
	 *
	 * @since !__version__!
	 *
	 * @see #reader(Version)
	 * @see #reader(Reader.Mode)
	 * @see #reader()
	 *
	 * @param version the GPX version to read
	 * @param mode the reading mode
	 * @return a new GPX reader object
	 * @throws NullPointerException if one of the arguments is {@code null}
	 */
	public static Reader reader(final Version version, final Mode mode) {
		final XMLReader<GPX> reader;
		switch (version) {
			case v10: reader = READER_v10; break;
			case v11: reader = READER; break;
			default: reader = READER; break;
		}

		return new Reader(reader, mode);
	}

	/**
	 * Return a GPX reader, reading GPX files (v1.1) with the given reading mode.
	 *
	 * @since !__version__!
	 *
	 * @see #reader(Version, Reader.Mode)
	 * @see #reader(Version)
	 * @see #reader()
	 *
	 * @param mode the reading mode
	 * @return a new GPX reader object
	 */
	public static Reader reader(final Mode mode) {
		return reader(Version.v11, mode);
	}

	/**
	 * Return a GPX reader, reading GPX files with the given version and in
	 * reading mode {@link Mode#STRICT}.
	 *
	 * @since !__version__!
	 *
	 * @see #reader(Version, Reader.Mode)
	 * @see #reader(Reader.Mode)
	 * @see #reader()
	 *
	 * @param version the GPX version to read
	 * @return a new GPX reader object
	 */
	public static Reader reader(final Version version) {
		return reader(version, Mode.STRICT);
	}

	/**
	 * Return a GPX reader, reading GPX files (v1.1) with reading mode
	 * {@link Mode#STRICT}.
	 *
	 * @since !__version__!
	 *
	 * @see #reader(Version, Reader.Mode)
	 * @see #reader(Version)
	 * @see #reader(Reader.Mode)
	 *
	 * @return a new GPX reader object
	 */
	public static Reader reader() {
		return reader(Version.v11, Mode.STRICT);
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
	 * @see #reader(Reader.Mode)
	 * @deprecated Use {@code GPX.reader(Mode.LENIENT).read(input)} instead
	 */
	@Deprecated
	public static GPX read(final InputStream input, final boolean lenient)
		throws IOException
	{
		return reader(Version.v11, lenient ? Mode.LENIENT : Mode.STRICT)
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
	 */
	public static GPX read(final InputStream input) throws IOException {
		return reader(Mode.STRICT).read(input);
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
	 * @see #reader(Reader.Mode)
	 * @deprecated Use {@code GPX.reader(Mode.LENIENT).read(path)} instead
	 */
	@Deprecated
	public static GPX read(final Path path, final boolean lenient)
		throws IOException
	{
		return reader(lenient ? Mode.LENIENT : Mode.STRICT).read(path);
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
		return reader(Mode.STRICT).read(path);
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
	 * @see #reader(Reader.Mode)
	 * @deprecated Use {@code GPX.reader(lenient).read(path)} instead
	 */
	@Deprecated
	public static GPX read(final String path, final boolean lenient)
		throws IOException
	{
		return reader(lenient ? Mode.LENIENT : Mode.STRICT).read(path);
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
		return reader(Mode.STRICT).read(path);
	}

}
