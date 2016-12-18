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
package jpx;

import static java.util.Objects.requireNonNull;
import static jpx.Lists.immutable;
import static jpx.XMLReader.attr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * GPX documents contain a metadata header, followed by way-points, routes, and
 * tracks. You can add your own elements to the extensions section of the GPX
 * document.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class GPX implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String VERSION = "1.1";

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
	 * Return an unmodifiable list of the {@code GPX} routes.
	 *
	 * @return an unmodifiable list of the {@code GPX} routes.
	 */
	public List<Route> getRoutes() {
		return _routes;
	}

	/**
	 * Return an unmodifiable list of the {@code GPX} tracks.
	 *
	 * @return an unmodifiable list of the {@code GPX} tracks.
	 */
	public List<Track> getTracks() {
		return _tracks;
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
		final Function<Object[], GPX> creator = a -> GPX.of(
			(String)a[0],
			(String)a[1],
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
		final XMLOutputFactory factory = XMLOutputFactory.newFactory();
		try {
			final XMLStreamWriter writer = factory.createXMLStreamWriter(output);
			gpx.write(writer);
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
	public static GPX read(final InputStream input)
		throws IOException
	{
		final XMLInputFactory factory = XMLInputFactory.newFactory();
		try {
			final XMLStreamReader reader = factory.createXMLStreamReader(input);
			if (reader.hasNext()) {
				reader.next();
				return reader().read(reader);
			} else {
				throw new IOException("No 'gpx' element found.");
			}
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}

}
