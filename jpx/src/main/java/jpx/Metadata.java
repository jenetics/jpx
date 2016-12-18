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

import static jpx.Lists.immutable;
import static jpx.Parsers.parseString;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Information about the GPX file, author, and copyright restrictions goes in
 * the metadata section. Providing rich, meaningful information about your GPX
 * files allows others to search for and use your GPS data.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version !__version__!
 * @since !__version__!
 */
public final class Metadata implements Serializable {

	private static final long serialVersionUID = 1L;

	private static DateTimeFormatter DTF =
		DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
		//DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private final String _name;
	private final String _description;
	private final Person _author;
	private final Copyright _copyright;
	private final List<Link> _links;
	private final ZonedDateTime _time;
	private final String _keywords;
	private final Bounds _bounds;

	/**
	 * Create a new {@code Metadata} object with the given parameters.
	 *
	 * @param name the name of the GPX file
	 * @param description a description of the contents of the GPX file
	 * @param author the person or organization who created the GPX file
	 * @param copyright copyright and license information governing use of the
	 *        file
	 * @param links URLs associated with the location described in the file
	 * @param time the creation date of the file
	 * @param keywords keywords associated with the file. Search engines or
	 *        databases can use this information to classify the data.
	 * @param bounds minimum and maximum coordinates which describe the extent
	 *        of the coordinates in the file
	 */
	private Metadata(
		final String name,
		final String description,
		final Person author,
		final Copyright copyright,
		final List<Link> links,
		final ZonedDateTime time,
		final String keywords,
		final Bounds bounds
	) {
		_name = name;
		_description = description;
		_author = author;
		_copyright = copyright;
		_links = immutable(links);
		_time = time;
		_keywords = keywords;
		_bounds = bounds;
	}

	/**
	 * Return the name of the GPX file.
	 *
	 * @return the name of the GPX file
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return a description of the contents of the GPX file.
	 *
	 * @return a description of the contents of the GPX file
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the person or organization who created the GPX file.
	 *
	 * @return the person or organization who created the GPX file
	 */
	public Optional<Person> getAuthor() {
		return Optional.ofNullable(_author);
	}

	/**
	 * Return the copyright and license information governing use of the file.
	 *
	 * @return the copyright and license information governing use of the file
	 */
	public Optional<Copyright> getCopyright() {
		return Optional.ofNullable(_copyright);
	}

	/**
	 * Return the URLs associated with the location described in the file. The
	 * returned list immutable.
	 *
	 * @return the URLs associated with the location described in the file
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the creation date of the file.
	 *
	 * @return the creation date of the file
	 */
	public Optional<ZonedDateTime> getTime() {
		return Optional.ofNullable(_time);
	}

	/**
	 * Return the keywords associated with the file. Search engines or databases
	 * can use this information to classify the data.
	 *
	 * @return the keywords associated with the file
	 */
	public Optional<String> getKeywords() {
		return Optional.ofNullable(_keywords);
	}

	/**
	 * Return the minimum and maximum coordinates which describe the extent of
	 * the coordinates in the file.
	 *
	 * @return the minimum and maximum coordinates which describe the extent of
	 *         the coordinates in the file
	 */
	public Optional<Bounds> getBounds() {
		return Optional.ofNullable(_bounds);
	}


	@Override
	public int hashCode() {
		int hash = 37;
		hash += 17*Objects.hashCode(_name) + 31;
		hash += 17*Objects.hashCode(_description) + 31;
		hash += 17*Objects.hashCode(_author) + 31;
		hash += 17*Objects.hashCode(_copyright) + 31;
		hash += 17*Objects.hashCode(_links) + 31;
		hash += 17*Objects.hashCode(_time) + 31;
		hash += 17*Objects.hashCode(_keywords) + 31;
		hash += 17*Objects.hashCode(_bounds) + 31;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Metadata &&
			Objects.equals(((Metadata)obj)._name, _name) &&
			Objects.equals(((Metadata)obj)._description, _description) &&
			Objects.equals(((Metadata)obj)._author, _author) &&
			Objects.equals(((Metadata)obj)._copyright, _copyright) &&
			Objects.equals(((Metadata)obj)._links, _links) &&
			Objects.equals(((Metadata)obj)._time, _time) &&
			Objects.equals(((Metadata)obj)._keywords, _keywords) &&
			Objects.equals(((Metadata)obj)._bounds, _bounds);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Metadata} object with the given parameters.
	 *
	 * @param name the name of the GPX file
	 * @param description a description of the contents of the GPX file
	 * @param author the person or organization who created the GPX file
	 * @param copyright copyright and license information governing use of the
	 *        file
	 * @param links URLs associated with the location described in the file
	 * @param time the creation date of the file
	 * @param keywords keywords associated with the file. Search engines or
	 *        databases can use this information to classify the data.
	 * @param bounds minimum and maximum coordinates which describe the extent
	 *        of the coordinates in the file
	 * @return a new {@code Metadata} object with the given parameters
	 * @throws NullPointerException if the given {@code links} sequence is
	 *        {@code null}
	 */
	public static Metadata of(
		final String name,
		final String description,
		final Person author,
		final Copyright copyright,
		final List<Link> links,
		final ZonedDateTime time,
		final String keywords,
		final Bounds bounds
	) {
		return new Metadata(
			name,
			description,
			author,
			copyright,
			links,
			time,
			keywords,
			bounds
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

		xml.write("metadata",
			xml.elem("name", _name),
			xml.elem("desc", _description),
			xml.elem(_author, Person::write),
			xml.elem(_copyright, Copyright::write),
			xml.elems(_links, Link::write),
			xml.elem("time", _time != null ? DTF.format(_time) : null),
			xml.elem("keywords", _keywords),
			xml.elem(_bounds, Bounds::write)
		);
	}

	@SuppressWarnings("unchecked")
	static XMLReader<Metadata> reader() {
		final Function<Object[], Metadata> create = a -> Metadata.of(
			parseString(a[0]),
			parseString(a[1]),
			(Person)a[2],
			(Copyright)a[3],
			(List<Link>)a[4],
			a[5] != null ? ZonedDateTime.parse((String)a[5], DTF) : null,
			(String)a[6],
			(Bounds)a[7]
		);

		return XMLReader.of(create, "metadata",
			XMLReader.of("name"),
			XMLReader.of("desc"),
			Person.reader(),
			Copyright.reader(),
			XMLReader.ofList(Link.reader()),
			XMLReader.of("time"),
			XMLReader.of("keywords"),
			Bounds.reader()
		);
	}

}
