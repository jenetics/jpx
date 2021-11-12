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
import static io.jenetics.jpx.Format.intString;
import static io.jenetics.jpx.Lists.copyOf;
import static io.jenetics.jpx.Lists.copyTo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Document;

import io.jenetics.jpx.GPX.Version;

/**
 * Represents a route - an ordered list of way-points representing a series of
 * turn points leading to a destination.
 * <p>
 * Create a new route via the builder:
 * <pre>{@code
 * final Route route = Route.builder()
 *     .name("Route 1")
 *     .description("Fancy mountain-bike tour.")
 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
 *     .build();
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.5
 * @since 1.0
 */
public final class Route implements Iterable<WayPoint>, Serializable {

	private static final long serialVersionUID = 2L;

	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final List<Link> _links;
	private final UInt _number;
	private final String _type;
	private final Document _extensions;
	private final List<WayPoint> _points;

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @param name the GPS name of the route
	 * @param comment the GPS comment of the route
	 * @param description the Text description of route for user. Not sent to GPS.
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about the route
	 * @param number the GPS route number
	 * @param type the type (classification) of the route
	 * @param extensions the extensions document
	 * @param points the sequence of route points
	 */
	private Route(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final Document extensions,
		final List<WayPoint> points
	) {
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = copyOf(links);
		_number = number;
		_type = type;
		_extensions = extensions;
		_points = copyOf(points);
	}

	/**
	 * Return the route name.
	 *
	 * @return the route name
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return the GPS comment of the route.
	 *
	 * @return the GPS comment of the route
	 */
	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	/**
	 * Return the Text description of route for user. Not sent to GPS.
	 *
	 * @return the Text description of route for user. Not sent to GPS
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the source of data. Included to give user some idea of reliability
	 * and accuracy of data.
	 *
	 * @return the source of data
	 */
	public Optional<String> getSource() {
		return Optional.ofNullable(_source);
	}

	/**
	 * Return the links to external information about the route.
	 *
	 * @return the links to external information about the route
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the GPS route number.
	 *
	 * @return the GPS route number
	 */
	public Optional<UInt> getNumber() {
		return Optional.ofNullable(_number);
	}

	/**
	 * Return the type (classification) of the route.
	 *
	 * @return the type (classification) of the route
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	/**
	 * Return the sequence of route points.
	 *
	 * @return the sequence of route points
	 */
	public List<WayPoint> getPoints() {
		return _points;
	}

	/**
	 * Return a stream of {@link WayPoint} objects this route contains.
	 *
	 * @return a stream of {@link WayPoint} objects this route contains
	 */
	public Stream<WayPoint> points() {
		return _points.stream();
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
	 * @throws org.w3c.dom.DOMException if the document could not be cloned,
	 *         because of an erroneous XML configuration
	 */
	public Optional<Document> getExtensions() {
		return Optional.ofNullable(_extensions).map(XML::clone);
	}

	@Override
	public Iterator<WayPoint> iterator() {
		return _points.iterator();
	}

	/**
	 * Convert the <em>immutable</em> route object into a <em>mutable</em>
	 * builder initialized with the current route values.
	 *
	 * @since 1.1
	 *
	 * @return a new route builder initialized with the values of {@code this}
	 *         route
	 */
	public Builder toBuilder() {
		return builder()
			.name(_name)
			.cmt(_comment)
			.desc(_description)
			.src(_source)
			.links(_links)
			.number(_number)
			.extensions(_extensions)
			.points(_points);
	}

	/**
	 * Return {@code true} if all route properties are {@code null} or empty.
	 *
	 * @return {@code true} if all route properties are {@code null} or empty
	 */
	public boolean isEmpty() {
		return _name == null &&
			_comment == null &&
			_description == null &&
			_source == null &&
			_links.isEmpty() &&
			_number == null &&
			_extensions == null &&
			_points.isEmpty();
	}

	/**
	 * Return {@code true} if not all route properties are {@code null} or empty.
	 *
	 * @since 1.1
	 *
	 * @return {@code true} if not all route properties are {@code null} or empty
	 */
	public boolean nonEmpty() {
		return !isEmpty();
	}

	@Override
	public int hashCode() {
		return hash(
			_name,
			_comment,
			_description,
			_source,
			_type,
			Lists.hashCode(_links),
			_number,
			_points
		);
	}

	@Override
	public boolean equals(final Object obj) {
		return obj == this ||
			obj instanceof Route &&
			Objects.equals(((Route)obj)._name, _name) &&
			Objects.equals(((Route)obj)._comment, _comment) &&
			Objects.equals(((Route)obj)._description, _description) &&
			Objects.equals(((Route)obj)._source, _source) &&
			Objects.equals(((Route)obj)._type, _type) &&
			Lists.equals(((Route)obj)._links, _links) &&
			Objects.equals(((Route)obj)._number, _number) &&
			Objects.equals(((Route)obj)._points, _points);
	}

	@Override
	public String toString() {
		return format("Route[name=%s, points=%s]", _name, _points.size());
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Return a new {@code Route} builder object.
	 *
	 * @return a new {@code Route} builder object
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder class for building {@code Route} objects.
	 * <pre>{@code
	 * final Route route = Route.builder()
	 *     .name("Route 1")
	 *     .description("Fancy mountain-bike tour.")
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
	 *     .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder implements Filter<WayPoint, Route> {

		private String _name;
		private String _comment;
		private String _description;
		private String _source;
		private final List<Link> _links = new ArrayList<>();
		private UInt _number;
		private String _type;
		private Document _extensions;
		private final List<WayPoint> _points = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Set the route name.
		 *
		 * @param name the route name.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder name(final String name) {
			_name = name;
			return this;
		}

		/**
		 * Return the current name value.
		 *
		 * @since 1.1
		 *
		 * @return the current name value
		 */
		public Optional<String> name() {
			return Optional.ofNullable(_name);
		}

		/**
		 * Set the route comment.
		 *
		 * @param comment the route comment
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder cmt(final String comment) {
			_comment = comment;
			return this;
		}

		/**
		 * Return the current comment value.
		 *
		 * @since 1.1
		 *
		 * @return the current comment value
		 */
		public Optional<String> cmt() {
			return Optional.ofNullable(_comment);
		}

		/**
		 * Set the route description.
		 *
		 * @param description the route description
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder desc(final String description) {
			_description = description;
			return this;
		}

		/**
		 * Return the current description value.
		 *
		 * @since 1.1
		 *
		 * @return the current description value
		 */
		public Optional<String> desc() {
			return Optional.ofNullable(_description);
		}

		/**
		 * Set the source of the data. Included to give user some idea of
		 * reliability and accuracy of data.
		 *
		 * @param source the source of the data
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder src(final String source) {
			_source = source;
			return this;
		}

		/**
		 * Return the current source value.
		 *
		 * @since 1.1
		 *
		 * @return the current source value
		 */
		public Optional<String> src() {
			return Optional.ofNullable(_source);
		}

		/**
		 * Set the links to additional information about the route. The link
		 * list may be {@code null}.
		 *
		 * @param links the links to additional information about the route
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the links in the list is
		 *         {@code null}
		 */
		public Builder links(final List<Link> links) {
			copyTo(links, _links);
			return this;
		}

		/**
		 * Set the links to external information about the route.
		 *
		 * @param link the links to external information about the route.
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code link} is {@code null}
		 */
		public Builder addLink(final Link link) {
			_links.add(requireNonNull(link));
			return this;
		}

		/**
		 * Set the links to external information about the route.
		 *
		 * @param href the links to external information about the route.
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code href} is
		 *         {@code null}
		 * @throws IllegalArgumentException if the given {@code href} is not a
		 *         valid URL
		 */
		public Builder addLink(final String href) {
			_links.add(Link.of(href));
			return this;
		}

		/**
		 * Return the current links. The returned link list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current links
		 */
		public List<Link> links() {
			return new NonNullList<>(_links);
		}

		/**
		 * Set the GPS route number.
		 *
		 * @param number the GPS route number
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder number(final UInt number) {
			_number = number;
			return this;
		}

		/**
		 * Set the GPS route number.
		 *
		 * @param number the GPS route number
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder number(final int number) {
			_number = UInt.of(number);
			return this;
		}

		/**
		 * Return the current number value.
		 *
		 * @since 1.1
		 *
		 * @return the current number value
		 */
		public Optional<UInt> number() {
			return Optional.ofNullable(_number);
		}

		/**
		 * Set the type (classification) of the route.
		 *
		 * @param type the type (classification) of the route.
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder type(final String type) {
			_type = type;
			return this;
		}

		/**
		 * Return the current type value.
		 *
		 * @since 1.1
		 *
		 * @return the current type value
		 */
		public Optional<String> type() {
			return Optional.ofNullable(_type);
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
		 * @param extensions the document
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
		 * Sets the way-points of the route. The way-point list may be
		 * {@code null}.
		 *
		 * @param points the way-points
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if one of the way-points is {@code null}
		 */
		public Builder points(final List<WayPoint> points) {
			copyTo(points, _points);
			return this;
		}

		/**
		 * Adds a way-point to the route.
		 *
		 * @param point the way-point which is added to the route
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the {@code point} is {@code null}
		 */
		public Builder addPoint(final WayPoint point) {
			_points.add(requireNonNull(point));
			return this;
		}

		/**
		 * Add a new way-point via the given {@code WayPoint.Builder} class.
		 *
		 * @param point the way-point builder
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder addPoint(final Consumer<WayPoint.Builder> point) {
			final WayPoint.Builder builder = WayPoint.builder();
			point.accept(builder);
			return addPoint(builder.build());
		}

		/**
		 * Return the current way-points. The returned list is mutable.
		 *
		 * @since 1.1
		 *
		 * @return the current, mutable way-point list
		 */
		public List<WayPoint> points() {
			return new NonNullList<>(_points);
		}

		@Override
		public Builder filter(final Predicate<? super WayPoint> predicate) {
			points(
				_points.stream()
					.filter(predicate)
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder map(
			final Function<? super WayPoint, ? extends WayPoint> mapper
		) {
			points(
				_points.stream()
					.map(mapper)
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder flatMap(
			final Function<
				? super WayPoint,
				? extends List<WayPoint>> mapper
		) {
			points(
				_points.stream()
					.flatMap(wp -> mapper.apply(wp).stream())
					.collect(Collectors.toList())
			);

			return this;
		}

		@Override
		public Builder listMap(
			final Function<
				? super List<WayPoint>,
				? extends List<WayPoint>> mapper
		) {
			points(mapper.apply(_points));

			return this;
		}

		/**
		 * Create a new {@code Route} object with the set values.
		 *
		 * @return a new {@code Route} object with the set values
		 */
		@Override
		public Route build() {
			return of(
				_name,
				_comment,
				_description,
				_source,
				_links,
				_number,
				_type,
				_extensions,
				_points
			);
		}

	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @since 1.5
	 *
	 * @param name the GPS name of the route
	 * @param comment the GPS comment of the route
	 * @param description the Text description of route for user. Not sent to GPS.
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about the route
	 * @param number the GPS route number
	 * @param type the type (classification) of the route
	 * @param extensions the extensions document
	 * @param points the sequence of route points
	 * @return a new route object with the given parameters
	 */
	public static Route of(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final Document extensions,
		final List<WayPoint> points
	) {
		return new Route(
			name,
			comment,
			description,
			source,
			links,
			number,
			type,
			XML.extensions(XML.clone(extensions)),
			points
		);
	}

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @param name the GPS name of the route
	 * @param comment the GPS comment of the route
	 * @param description the Text description of route for user. Not sent to GPS.
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about the route
	 * @param number the GPS route number
	 * @param type the type (classification) of the route
	 * @param points the sequence of route points
	 * @return a new route object with the given parameters
	 */
	public static Route of(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final List<WayPoint> points
	) {
		return of(
			name,
			comment,
			description,
			source,
			links,
			number,
			type,
			null,
			points
		);
	}

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @param name the GPS name of the route
	 * @param points the sequence of route points
	 * @return a new route object with the given parameters
	 */
	public static Route of(
		final String name,
		final List<WayPoint> points
	) {
		return of(
			name,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			points
		);
	}

	/**
	 * Create a new {@code Route} with the given parameters and way-points.
	 *
	 * @param points the sequence of route points
	 * @return a new route object with the given parameters
	 */
	public static Route of(final List<WayPoint> points) {
		return of(
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			points
		);
	}


	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private Object writeReplace() {
		return new SerialProxy(SerialProxy.ROUTE, this);
	}

	private void readObject(final ObjectInputStream stream)
		throws InvalidObjectException
	{
		throw new InvalidObjectException("Serialization proxy required.");
	}

	void write(final DataOutput out) throws IOException {
		IO.writeNullableString(_name, out);
		IO.writeNullableString(_comment, out);
		IO.writeNullableString(_description, out);
		IO.writeNullableString(_source, out);
		IO.writes(_links, Link::write, out);
		IO.writeNullable(_number, UInt::write, out);
		IO.writeNullableString(_type, out);
		IO.writeNullable(_extensions, IO::write, out);
		IO.writes(_points, WayPoint::write, out);
	}

	static Route read(final DataInput in) throws IOException {
		return new Route(
			IO.readNullableString(in),
			IO.readNullableString(in),
			IO.readNullableString(in),
			IO.readNullableString(in),
			IO.reads(Link::read, in),
			IO.readNullable(UInt::read, in),
			IO.readNullableString(in),
			IO.readNullable(IO::readDoc, in),
			IO.reads(WayPoint::read, in)
		);
	}

	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	private static String url(final Route route) {
		return route.getLinks().isEmpty()
			? null
			: route.getLinks().get(0).getHref().toString();
	}

	private static String urlname(final Route route) {
		return route.getLinks().isEmpty()
			? null
			: route.getLinks().get(0).getText().orElse(null);
	}

	// Define the needed writers for the different versions.
	private static final XMLWriters<Route> WRITERS = new XMLWriters<Route>()
		.v00(XMLWriter.elem("name").map(r -> r._name))
		.v00(XMLWriter.elem("cmt").map(r -> r._comment))
		.v00(XMLWriter.elem("desc").map(r -> r._description))
		.v00(XMLWriter.elem("src").map(r -> r._source))
		.v11(XMLWriter.elems(Link.WRITER).map(r -> r._links))
		.v10(XMLWriter.elem("url").map(Route::url))
		.v10(XMLWriter.elem("urlname").map(Route::urlname))
		.v00(XMLWriter.elem("number").map(r -> intString(r._number)))
		.v00(XMLWriter.elem("type").map(r -> r._type))
		.v00(XMLWriter.doc("extensions").map(gpx -> gpx._extensions))
		.v10(XMLWriter.elems(WayPoint.xmlWriter(Version.V10, "rtept")).map(r -> r._points))
		.v11(XMLWriter.elems(WayPoint.xmlWriter(Version.V11, "rtept")).map(r -> r._points));


	// Define the needed readers for the different versions.
	private static final XMLReaders READERS = new XMLReaders()
		.v00(XMLReader.elem("name"))
		.v00(XMLReader.elem("cmt"))
		.v00(XMLReader.elem("desc"))
		.v00(XMLReader.elem("src"))
		.v11(XMLReader.elems(Link.READER))
		.v10(XMLReader.elem("url").map(Format::parseURI))
		.v10(XMLReader.elem("urlname"))
		.v00(XMLReader.elem("number").map(UInt::parse))
		.v00(XMLReader.elem("type"))
		.v00(XMLReader.doc("extensions"))
		.v10(XMLReader.elems(WayPoint.xmlReader(Version.V10, "rtept")))
		.v11(XMLReader.elems(WayPoint.xmlReader(Version.V11, "rtept")));

	static XMLWriter<Route> xmlWriter(final Version version) {
		return XMLWriter.elem("rte", WRITERS.writers(version));
	}

	@SuppressWarnings("unchecked")
	static XMLReader<Route> xmlReader(final Version version) {
		return XMLReader.elem(
			version == Version.V10 ? Route::toRouteV10 : Route::toRouteV11,
			"rte",
			READERS.readers(version)
		);
	}

	@SuppressWarnings("unchecked")
	private static Route toRouteV11(final Object[] v) {
		return new Route(
			(String)v[0],
			(String)v[1],
			(String)v[2],
			(String)v[3],
			(List<Link>)v[4],
			(UInt)v[5],
			(String)v[6],
			XML.extensions((Document)v[7]),
			(List<WayPoint>)v[8]
		);
	}

	@SuppressWarnings("unchecked")
	private static Route toRouteV10(final Object[] v) {
		return new Route(
			(String)v[0],
			(String)v[1],
			(String)v[2],
			(String)v[3],
			v[4] != null
				? List.of(Link.of((URI)v[4], (String)v[5], null))
				: null,
			(UInt)v[6],
			(String)v[7],
			XML.extensions((Document)v[8]),
			(List<WayPoint>)v[9]
		);
	}

}
